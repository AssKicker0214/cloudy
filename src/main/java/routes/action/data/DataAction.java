package routes.action.data;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import model.data.AbstractFile;
import response.NotFound;
import routes.Restful;
import routes.Routing;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Routing("/data-api/v1(/?|/.*)")
public class DataAction implements Restful {
    @Override
    public HttpResponse get(HttpRequest req, String... args) throws IOException {
        String sub = args[0].replaceFirst("/", "");
        Optional<AbstractFile> opt = AbstractFile.get(Paths.get(sub));
        if (!opt.isPresent()) return NotFound.response(sub);
        // TODO read permission check
        AbstractFile file = opt.get();
        return file.makeResponse();
    }
}
