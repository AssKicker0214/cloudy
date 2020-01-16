package routes.action.data;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import response.NotFound;
import response.PlainText;
import routes.DefaultEndpoint;
import routes.Restful;
import routes.Routing;

import java.io.IOException;

@SuppressWarnings("unused")
@Routing("/meta/([^/]*)/(.*)")
public class Meta extends DefaultEndpoint implements Restful {

    @Override
    public HttpResponse get(FullHttpRequest req, String... args) throws IOException {
        String group = args[0].toLowerCase();
        String target = args[1];
        if (group.equals("env") || group.equals("environment")) {
            String val = System.getenv(target);
            return PlainText.response(val == null ? "" : val);
        }
        return NotFound.response("");
    }

}
