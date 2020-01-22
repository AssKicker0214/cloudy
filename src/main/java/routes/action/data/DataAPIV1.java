package routes.action.data;

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

import java.nio.charset.StandardCharsets;
import java.util.List;
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
        String filename = req.content().toString(StandardCharsets.UTF_8);
        String path = (directory + "/" + filename).replaceAll("//", "/");

        Optional<Character> opt = Storage.type(path);
        if (opt.isPresent()) return Conflict.response("Already exists");

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
