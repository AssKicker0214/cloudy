package routes;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import response.MethodNotAllowed;
import response.NotFound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class DefaultEndpoint implements Restful{

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    public HttpResponse get(HttpRequest req, String... args) throws IOException{
        System.err.println("Made default response");
        return NotFound.response("");
    }

    public HttpResponse post(HttpRequest req, String... args){
        return MethodNotAllowed.response("");
    }

    public HttpResponse delete(HttpRequest req, String... args) {
        return MethodNotAllowed.response("");
    }

    protected HttpResponse sendFile(Path path) {
        byte[] bytes = {};
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(bytes)
        );
        res.headers().set("Content-Length", bytes.length);
        return res;
    }

    public HttpChunkedInput getChunk() {
        throw new NoChunksException();
    }
    
}
