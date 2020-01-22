package response;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

public class PlainText extends DefaultFullHttpResponse {

    public PlainText(String text) {
        super(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(text.getBytes(StandardCharsets.UTF_8)));
        setHeaders();
    }

    public PlainText(byte[] bytes) {
        super(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
        setHeaders();
    }

    private void setHeaders(){
        this.headers().set("Content-Length", this.content().array().length);
        this.headers().set("Content-Type", "text/plain");
    }

    public static FullHttpResponse response(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
        res.headers().set("Content-Length", bytes.length);
        res.headers().set("Content-Type", "text/plain");
        return res;
    }
}
