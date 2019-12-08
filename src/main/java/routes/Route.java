package routes;

import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static io.netty.handler.codec.http.HttpMethod.*;

public abstract class Route {
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    public HttpResponse process(HttpRequest req, String... args) {
        HttpMethod method = req.method();
        try {
            if (method == GET) return setDateHeader(get(req, args));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_ACCEPTABLE);
    }

    protected HttpResponse get(HttpRequest req, String... args) throws IOException{
        System.err.println("Made default response");
        return new response.NotFound();
    }

    public static HttpResponse setDateHeader(HttpResponse res) {
        SimpleDateFormat formatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.CHINA);
        formatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        res.headers().set("Date", formatter.format(time.getTime()));
        return res;
    }
}
