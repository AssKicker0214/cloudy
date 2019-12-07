package routes;

import io.netty.handler.codec.http.*;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpMethod.*;

public abstract class Route {

    public HttpResponse process(HttpRequest req, String... args) {
        HttpMethod method = req.method();
        try {
            if (method == GET) return get(req, args);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_ACCEPTABLE);
    }

    protected HttpResponse get(HttpRequest req, String... args) throws IOException {
        // 404
        System.out.println("default action");
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }
}
