package routes.action.data;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import model.data.AbstractFile;
import response.Conflict;
import response.Created;
import response.NotFound;
import response.OK;
import routes.DefaultEndpoint;
import routes.Restful;
import routes.Routing;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Routing("/data-api/v1(/?|/.*)")
public class DataAPIV1 extends DefaultEndpoint implements Restful {
    @Override
    public HttpResponse get(HttpRequest req, String... args) throws IOException {
        String sub = args[0].replaceFirst("/", "");
        Optional<AbstractFile> opt = AbstractFile.get(Paths.get(sub));
        if (!opt.isPresent()) return NotFound.response(sub);
        // TODO read permission check
        AbstractFile file = opt.get();
        return file.downloadResponse();
    }

    @Override
    public HttpResponse post(HttpRequest req, String... args) {
        HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(req);
        // TODO enable small file uploading directly through post
        String sub = args[0].replaceFirst("/", "");
        boolean newlyCreated = AbstractFile.getOrCreate(Paths.get(sub));
        return newlyCreated ? Created.response(sub) : Conflict.response(sub);
    }

    @Override
    public HttpResponse delete(HttpRequest req, String... args) {
        String sub = args[0].replaceFirst("/", "");
        Optional<AbstractFile> opt = AbstractFile.get(Paths.get(sub));
        if (opt.isPresent()) {
            AbstractFile af = opt.get();
            boolean deleted = af.delete();
            if (deleted) {
                return OK.response(sub);
            }
        }
        return NotFound.response(sub);
    }
}
