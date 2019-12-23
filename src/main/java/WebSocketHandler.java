import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import model.ws.WSGroup;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    private WSGroup group;

    public WebSocketHandler(String uri) {
        this.group = WSGroup.getModel(uri);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.group.register(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof WebSocketFrame) {
            this.group.sendToAll((WebSocketFrame) msg);
        }
    }
}
