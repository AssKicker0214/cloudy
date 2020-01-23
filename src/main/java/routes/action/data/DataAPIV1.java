package routes.action.data;

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import model.data.FileInfo;
import model.storage.*;
import model.ws.ControlMessage;
import model.ws.ControlWSGroup;
import response.*;
import routes.DefaultEndpoint;
import routes.Restful;
import routes.Routing;
import utils.Config;
import utils.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
@Routing("/data-api/v1(/?|/.*)")
public class DataAPIV1 extends DefaultEndpoint implements Restful {
    @Override
    public HttpResponse get(FullHttpRequest req, String... args) {
        String path = args[0];
        Optional<Character> opt = Storage.type(path);

        // if file does not exists, or is not allowed to show
        if (!opt.isPresent()) return NotFound.response(path);

        char type = opt.get();
        if (type == Storage.TYPE_DIRECTORY) {
            DirStorage directory = Storage.getDirectory(path);
            if (directory == null) return NotFound.response(path);
            List<FileInfo> list = directory.list();
            return ApplicationJson.response(list);
        } else {
            // type == Storage.TYPE_FILE
            return BinaryDownload.response(Storage.getRealPath(path));
        }
    }

    @Override
    public HttpResponse post(FullHttpRequest req, String... args) {
        String directory = args[0];
        String syncInfo = req.content().toString(StandardCharsets.UTF_8);
        Map<String, String> syncMap = JsonUtil.fromJson(syncInfo, new TypeReference<Map<String, String>>() {
        });
        assert syncMap != null;
        String name = syncMap.get("name");                  // file name
        String sha256 = syncMap.get("sha256");              // file hash
        long size = Long.parseLong(syncMap.get("size"));   // file size

        String path = (directory + "/" + name).replaceAll("//", "/");

        Optional<Character> opt = Storage.type(path);

        // not exists
        if (!opt.isPresent()) {
            return uploadNewFileResponse(path);
        }

        // exists, and is a directory
        if (opt.get() == Storage.TYPE_DIRECTORY) {
            return NotAcceptable.response("Directory upload is not supported");
        }

        // exists, and is a regular file
        if (opt.get() == Storage.TYPE_FILE) {
            // check if file size equals
            long sizeFromSvr = Storage.fileSize(path);
            boolean sizeEquals = sizeFromSvr == size;
            String hashFromSvr = Storage.fileHash(path).orElse("");
            if (sizeEquals) {
                // if file size equals, use HASH to check if they are the same file
                // same file
                if (hashFromSvr.equalsIgnoreCase(sha256)) return OK.response("Already there");

                // not the same
                Optional<Deletable> toDelete = Storage.toDelete(path);
                assert toDelete.isPresent();
                if (toDelete.get().delete()) {
                    return uploadNewFileResponse(path);
                } else {
                    return InternalServerError.response("cannot delete original file with the same name");
                }
            } else {
                // the regular file on server is of different size from client
                return Conflict.response(String.format(
                        "{\"hash\": \"%s\", \"size\": %d}",
                        hashFromSvr, sizeFromSvr
                ));
                // the client will do a partial file hashing, and decide what to do next.
            }
        }

        return BadRequest.response("");
    }

    private HttpResponse uploadNewFileResponse(String path) {
        FileStorage file = Storage.createFile(path);
        HttpResponse res = Created.response(path);
        res.headers().set("Max-Block-Size", Config.getIntOrDefault("MAX_WEBSOCKET_FRAME_SIZE", 65536));
        return res;
    }

    @Override
    public HttpResponse put(FullHttpRequest req, String... args) {
        String dst = args[0];
        boolean successful = false;

        if (req.headers().contains("Rename-From")) {
            // MOVE
            String src = req.headers().get("Rename-From");
            Optional<Renamable> opt = Storage.toRename(src);
            if (!opt.isPresent()) return NotFound.response(src);
            successful = opt.get().rename(dst);
        }


        if (req.headers().contains("File-Type")) {
            String type = req.headers().get("File-Type");
            if (type.equalsIgnoreCase("Directory")) {
                // Create new directory
                DirStorage directory = Storage.createDirectory(dst);
                successful = directory != null;
            } else {
                // File put is not yet supported
                return NotAcceptable.response("File PUT is not yet supported");
            }
        }

        if (successful) refreshAllClient(dst.substring(0, dst.lastIndexOf('/') + 1));
        return successful ? Created.response(dst) : InternalServerError.response("");
    }

    @Override
    public HttpResponse delete(FullHttpRequest req, String... args) {
        String path = args[0];
        Optional<Deletable> opt = Storage.toDelete(path);
        if (!opt.isPresent()) return NotFound.response(path);

        boolean successful = opt.get().delete();
        if (successful) refreshAllClient(path.substring(0, path.lastIndexOf('/') + 1));

        return successful ? NoContent.response(path) : InternalServerError.response("Fail to delete " + path);
    }

    private void refreshAllClient(String relatedDirectory) {
        ControlWSGroup.inst().sendToAll(new ControlMessage(
                "REFRESH_LIST",
                relatedDirectory
        ));
    }
}
