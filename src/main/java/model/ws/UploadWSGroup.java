package model.ws;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class UploadWSGroup extends WSGroup {
    private WebSocketFrame cache;

    private UploadWSGroup(){
    }

    public static WSGroup inst() {
        return new UploadWSGroup();
    }

    @Override
    public void register(Channel channel) {
        super.register(channel);
        if (this.cache != null) {
            channel.writeAndFlush(this.cache);
        }
    }

    @Override
    public synchronized void onReceive(WebSocketFrame msg) {
        this.cache = msg;
        // TODO
    }
}
