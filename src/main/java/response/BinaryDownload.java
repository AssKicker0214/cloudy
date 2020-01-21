package response;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinaryDownload {
    public static HttpResponse response(Path abs) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(abs);
            return response(bytes, "application/octet-stream", abs.getFileName().toString());
        } catch (IOException e) {
            e.printStackTrace();
            return InternalServerError.response(e.getMessage());
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return InternalServerError.response("Large file download is not yet supported, use nginx service on port 8080");
        }
    }

    public static HttpResponse response(byte[] bytes, String contentType, String filename) {
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
        res.headers().set("Content-Type", contentType);
        res.headers().set("Content-Disposition", "attachment; " + filename);
        res.headers().set("Content-Length", bytes.length);
        return res;
    }
}
