package routes;

import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpMethod.*;

public abstract class BaseRoute {

    public HttpResponse process(HttpRequest req) {
        HttpMethod method = req.method();
        if (method == GET)  return get(req);
        return null;
    }

    protected HttpResponse get(HttpRequest req){
        // 404
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }
}
