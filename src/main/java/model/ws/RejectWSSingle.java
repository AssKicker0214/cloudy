package model.ws;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class RejectWSSingle extends WSGroup {

    @Override
    public void onReceive(WebSocketFrame msg) {
        this.channels.writeAndFlush(msg);
    }

    @Override
    public void register(Channel channel) {
        channel.close();
    }
}
