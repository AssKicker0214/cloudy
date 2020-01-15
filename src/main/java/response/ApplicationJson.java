package response;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.JsonUtil;

import java.nio.charset.StandardCharsets;


public class ApplicationJson {
    public static HttpResponse response(Object any){
        String json = JsonUtil.toJson(any);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
        res.headers().set("Content-Length", bytes.length);
        res.headers().set("Content-Type", "application/json");
        return res;
    }
}
