package response;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class NotFound extends DefaultHttpResponse {

    public NotFound(){
        super(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        this.headers().set("Content-Length", 0);
    }
}
