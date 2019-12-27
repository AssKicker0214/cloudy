import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import response.MethodNotAllowed;
import response.NotFound;
import routes.DefaultEndpoint;
import routes.RestCall;
import routes.Restful;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


public class HTTPHandler extends ChannelInboundHandlerAdapter {

    private Router router = Router.newInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if(withHeader(req, "Connection", "Upgrade") && withHeader(req, "Upgrade", "WebSocket")){
                // ws
                ctx.pipeline().replace(this, "WebSocketHandler", new WebSocketHandler(req.uri()));
                this.handleWSHandShake(ctx, req);
            } else {
                // http
                System.out.println(req.uri());
                /*HttpResponse res = router.dispatch(req);
                res = this.setDateHeader(res);
                ctx.write(res);*/

                HttpResponse res;
                RestCall call = RestCall.parseURI(req.uri());
                if(call == null){
                    res = NotFound.response("");
                    ctx.write(res);
                    return;
                }
                Restful endpoint = call.getEndpoint();
                String[] args = call.getArgs();
//                assert req.method().equals(HttpMethod.GET);
                if (req.method().equals(HttpMethod.GET)) {
                    res = endpoint.get(req, args);
                } else if (req.method().equals(HttpMethod.POST)) {
                    res = endpoint.post(req, args);
                }else{
                    res = MethodNotAllowed.response(req.method().toString());
                }
                res = this.setDateHeader(res);
                ctx.write(res);
                if (withHeader(res, "Transfer-Encoding", "chunked")) {
                    ctx.pipeline().addBefore("HTTPHandler", "ChunkWriterHandler", new ChunkedWriteHandler());
                    HttpChunkedInput chunk = endpoint.getChunk();
                    ctx.write(chunk);
                }
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

    private boolean withHeader(HttpMessage msg, String name, String value) {
        return msg.headers().contains(name) && msg.headers().get(name).equalsIgnoreCase(value);
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
        SimpleDateFormat formatter = new SimpleDateFormat(DefaultEndpoint.HTTP_DATE_FORMAT, Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone(DefaultEndpoint.HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        res.headers().set("Date", formatter.format(time.getTime()));
        return res;
    }
}
