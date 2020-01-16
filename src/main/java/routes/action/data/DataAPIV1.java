package routes.action.data;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import model.data.AbstractFile;
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
import java.util.Optional;

@Routing("/data-api/v1(/?|/.*)")
public class DataAPIV1 extends DefaultEndpoint implements Restful {
    @Override
    public HttpResponse get(FullHttpRequest req, String... args) throws IOException {
        String sub = args[0].replaceFirst("/", "");
        Optional<AbstractFile> opt = AbstractFile.get(Paths.get(sub));
        if (!opt.isPresent()) return NotFound.response(sub);
        // TODO read permission check
        AbstractFile file = opt.get();
        return file.downloadResponse();
    }

    @Override
    public HttpResponse post(FullHttpRequest req, String... args) {
        String filename = req.content().toString(StandardCharsets.UTF_8);
        String directory = args[0].replaceFirst("/", "");
        Path sub = Paths.get(directory).resolve(filename);
        boolean newlyCreated = AbstractFile.getOrCreate(sub);
        HttpResponse res = newlyCreated ? Created.response(sub.toString()) : Conflict.response(sub.toString());
        res.headers().setInt("Max-Block-Size", Config.getIntOrDefault("MAX_WEBSOCKET_FRAME_SIZE", 65536));
        return res;
    }

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
                                "REFRESH_DIRECTORY",
                                args[0].substring(0, args[0].lastIndexOf('/')+1))
                );
                return NoContent.response(sub);
            }
        }
        return NotFound.response(sub);
    }
}
