package model.ws;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import utils.StringUtil;

public abstract class WSGroup {
    protected ChannelGroup channels;

    public static WSGroup getModel(String uri) {
        String topic = StringUtil.strip(uri.trim(), '/').toLowerCase();
        switch (topic) {
            case "clipboard":
                return ClipboardWSGroup.inst();
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

    public void sendToAll(WebSocketFrame msg) {
        this.channels.writeAndFlush(msg);
    }
}
