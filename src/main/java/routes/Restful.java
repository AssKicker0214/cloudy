package routes;

import io.netty.handler.codec.http.HttpResponse;

import io.netty.handler.codec.http.HttpRequest;

import java.io.IOException;

public interface Restful {
    HttpResponse get(HttpRequest req, String... args) throws IOException;
}
