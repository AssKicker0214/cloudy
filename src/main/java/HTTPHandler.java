import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import routes.Route;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class HTTPHandler extends ChannelInboundHandlerAdapter {

    private Router router = Router.newInstance();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            System.out.println(req.uri());
//            byte[] responseBytes = req.uri().getBytes();
//            FullHttpResponse res = new DefaultFullHttpResponse(
//                    HttpVersion.HTTP_1_1,
//                    HttpResponseStatus.OK,
//                    Unpooled.wrappedBuffer(responseBytes)
//            );
            HttpResponse res = router.dispatch(req);
//            res.headers().set("Content-Type", "text/plain");
//            res.headers().set("Content-Length", responseBytes.length);
            res = this.setDateHeader(res);
            ctx.write(res);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public HttpResponse setDateHeader(HttpResponse res) {
        SimpleDateFormat formatter = new SimpleDateFormat(Route.HTTP_DATE_FORMAT, Locale.CHINA);
        formatter.setTimeZone(TimeZone.getTimeZone(Route.HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        res.headers().set("Date", formatter.format(time.getTime()));
        return res;
    }
}
