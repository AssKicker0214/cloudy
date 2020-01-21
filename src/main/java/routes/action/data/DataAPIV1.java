package routes.action.data;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import model.data.AbstractFile;
import model.data.FileInfo;
import model.storage.DirStorage;
import model.storage.FileStorage;
import model.storage.Storage;
import model.ws.ControlWSGroup;
import model.ws.ControlMessage;
import response.*;
import routes.DefaultEndpoint;
import routes.Restful;
import routes.Routing;
import utils.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Routing("/data-api/v1(/?|/.*)")
public class DataAPIV1 extends DefaultEndpoint implements Restful {
    @Override
    public HttpResponse get(FullHttpRequest req, String... args) throws IOException {
        /*String sub = args[0].replaceFirst("/", "");
        Optional<AbstractFile> opt = AbstractFile.get(Paths.get(sub));
        if (!opt.isPresent()) return NotFound.response(sub);
        // TODO read permission check
        AbstractFile file = opt.get();
        return file.downloadResponse();*/
        String path = args[0];
        Optional<Character> opt = Storage.type(path);

        // if file does not exists, or
        if(!opt.isPresent()) return NotFound.response(path);

        char type = opt.get();
        if(type == Storage.TYPE_DIRECTORY){
            DirStorage directory = Storage.getDirectory(path);
            if(directory == null) return NotFound.response(path);
            List<FileInfo> list = directory.list();
            return ApplicationJson.response(list);
        }else{
            // type == Storage.TYPE_FILE
            return BinaryDownload.response(Storage.getRealPath(path));
        }
    }

    @Override
    public HttpResponse post(FullHttpRequest req, String... args) {
        /*String filename = req.content().toString(StandardCharsets.UTF_8);
        String directory = args[0].replaceFirst("/", "");
        Path sub = Paths.get(directory).resolve(filename);
        boolean newlyCreated = AbstractFile.getOrCreate(sub);
        HttpResponse res = newlyCreated ? Created.response(sub.toString()) : Conflict.response(sub.toString());
        res.headers().setInt("Max-Block-Size", Config.getIntOrDefault("MAX_WEBSOCKET_FRAME_SIZE", 65536));
        return res;*/
        String directory = args[0];
        String filename = req.content().toString(StandardCharsets.UTF_8);
        String path = (directory + "/" + filename).replaceAll("//", "/");

        Optional<Character> opt = Storage.type(path);
        if(opt.isPresent()) return Conflict.response("Already exists");

        FileStorage file = Storage.createFile(path);
        HttpResponse res = Created.response(path);
        res.headers().set("Max-Block-Size", Config.getIntOrDefault("MAX_WEBSOCKET_FRAME_SIZE", 65536));
        return res;
    }

    /*@Override
    public HttpResponse put(FullHttpRequest req, String... args) {
        String sub = args[0];
        if (req.headers().contains("File-Type") && req.headers().get("File-Type").equals("directory")) {
            // "File-Type: directory"
            Optional<AbstractFile> opt = AbstractFile.get(sub);
            if(opt.isPresent()) return Conflict.response("Directory already exists");

        }
    }*/

    @Override
    public HttpResponse delete(FullHttpRequest req, String... args) {
        String sub = args[0].replaceFirst("/", "");
        Optional<AbstractFile> opt = AbstractFile.get(Paths.get(sub));
        if (opt.isPresent()) {
            AbstractFile af = opt.get();
            boolean deleted = af.delete();
            if (deleted) {
                ControlWSGroup.inst().sendToAll(
                        new ControlMessage(
                                "REFRESH_LIST",
                                args[0].substring(0, args[0].lastIndexOf('/')+1))
                );
                return NoContent.response(sub);
            }
        }
        return NotFound.response(sub);
    }
}
