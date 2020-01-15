package model.ws;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import utils.JsonUtil;

import java.io.Serializable;

public class BroadcasterWSGroup extends WSGroup {

    static class Holder{
        static final BroadcasterWSGroup inst = new BroadcasterWSGroup();
    }

    private BroadcasterWSGroup(){
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
