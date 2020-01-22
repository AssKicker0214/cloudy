package routes;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.io.IOException;
import java.util.Optional;

public interface Restful {
    HttpResponse get(FullHttpRequest req, String... args) throws IOException;

    HttpResponse post(FullHttpRequest req, String... args);

    HttpResponse delete(FullHttpRequest req, String... args);

    HttpResponse put(FullHttpRequest req, String... args);


    HttpChunkedInput getChunk();

}
