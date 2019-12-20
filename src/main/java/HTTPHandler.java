import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
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

            if (req.headers().get("Connection").equalsIgnoreCase("Upgrade")
                    && req.headers().get("Upgrade").equalsIgnoreCase("WebSocket")) {
                // ws
                ctx.pipeline().replace(this, "WebSocketHandler", new WebSocketHandler());
                this.handleWSHandShake(ctx, req);
            } else {
                // http
                System.out.println(req.uri());
                HttpResponse res = router.dispatch(req);
                res = this.setDateHeader(res);
                ctx.write(res);
            }

        }
    }

    private void handleWSHandShake(ChannelHandlerContext ctx, HttpRequest req) {
        System.out.println("Start ws handshake");

        String wsURL = String.format("ws://%s%s", req.headers().get("Host"), req.uri());
        System.out.println("ws url: " + wsURL);
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wsURL, null, true);
        WebSocketServerHandshaker shaker = wsFactory.newHandshaker(req);
        if (shaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            shaker.handshake(ctx.channel(), req);
        }
        System.out.println("End ws handshake");
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
        SimpleDateFormat formatter = new SimpleDateFormat(Route.HTTP_DATE_FORMAT, Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone(Route.HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        res.headers().set("Date", formatter.format(time.getTime()));
        return res;
    }
}
