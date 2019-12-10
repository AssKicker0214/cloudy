package routes;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.io.IOException;

public abstract class Route implements Restful{

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    public HttpResponse get(HttpRequest req, String... args) throws IOException{
        System.err.println("Made default response");
        return new response.NotFound();
    }
}
