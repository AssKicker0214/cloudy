package model.ws;

import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class EchoWSSingle extends WSGroup {

    @Override
    public void onReceive(WebSocketFrame msg) {
        this.channels.writeAndFlush(msg);
    }
}
