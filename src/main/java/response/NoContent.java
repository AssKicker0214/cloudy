package response;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

public class NoContent {

    public static HttpResponse response(String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT, Unpooled.wrappedBuffer(bytes));
        res.headers().set("Content-Length", bytes.length);
        return res;
    }
}
