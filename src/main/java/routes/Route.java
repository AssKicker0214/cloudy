package routes;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Route implements Restful{

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    public HttpResponse get(HttpRequest req, String... args) throws IOException{
        System.err.println("Made default response");
        return new response.NotFound();
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
    
}
