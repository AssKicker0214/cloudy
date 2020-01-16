package model.ws;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class ControlWSGroup extends WSGroup {

    static class Holder{
        static final ControlWSGroup inst = new ControlWSGroup();
    }

    private ControlWSGroup(){
    }

    public static WSGroup inst() {
        return Holder.inst;
    }

    @Override
    public void register(Channel channel) {
        super.register(channel);
    }

    @Override
    public synchronized void onReceive(WebSocketFrame msg) {
        // only for test reason
        this.channels.writeAndFlush(msg);
    }


}
