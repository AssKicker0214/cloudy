package routes.action.data;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import model.data.AbstractFile;
import response.NotFound;
import routes.DefaultEndpoint;
import routes.Restful;
import routes.Routing;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Routing("/data-api/meta(/?|/.*)")
public class DataAPIMeta extends DefaultEndpoint implements Restful {
    @Override
    public HttpResponse get(HttpRequest req, String... args) throws IOException {
        return null;
    }

    private static class MetaData{
        // bytes

    }
}
