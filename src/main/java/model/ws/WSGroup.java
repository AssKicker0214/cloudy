package model.ws;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import utils.JsonUtil;
import utils.StringUtil;

public abstract class WSGroup {
    protected ChannelGroup channels;

    public static WSGroup getModel(String uri) {
        String topic = StringUtil.strip(uri.trim(), '/').toLowerCase();
        switch (topic) {
            case "clipboard":
                return ClipboardWSGroup.inst();
            case "upload":
                return UploadWSGroup.inst();
            case "broadcaster":
                return BroadcasterWSGroup.inst();
            default:
                return new EchoWSSingle();
        }
    }

    protected WSGroup(){
        this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void register(Channel channel) {
        this.channels.add(channel);
    }

    public abstract void onReceive(WebSocketFrame msg) ;

    public void handleException(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.toString());
    }

    public void sendToAll(Object obj) {
        String json = JsonUtil.toJson(obj);
        WebSocketFrame frame = new TextWebSocketFrame(json);
        this.channels.writeAndFlush(frame);
    }
}
