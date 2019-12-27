package routes;

import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.io.IOException;
import java.util.Optional;

public interface Restful {
    HttpResponse get(HttpRequest req, String... args) throws IOException;

    HttpResponse post(HttpRequest req, String... args);

    HttpResponse delete(HttpRequest req, String... args);

    HttpChunkedInput getChunk();

}
