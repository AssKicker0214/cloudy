package model.ws;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class ClipboardWSGroup extends WSGroup {
    private WebSocketFrame cache;

    static class Holder{
        static final ClipboardWSGroup inst = new ClipboardWSGroup();
    }

    private ClipboardWSGroup(){
    }

    public static WSGroup inst() {
        return Holder.inst;
    }

    @Override
    public void register(Channel channel) {
        super.register(channel);
        if (this.cache != null) {
            channel.writeAndFlush(this.cache);
        }
    }

    @Override
    public synchronized void sendToAll(WebSocketFrame msg) {
        this.cache = msg;
        super.sendToAll(msg);
    }
}
