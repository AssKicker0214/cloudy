package model.ws;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import model.data.AbstractFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

public class UploadWSGroup extends WSGroup {
    private String sub;
    private RandomAccessFile raf;

    private UploadWSGroup() {
    }

    public static WSGroup inst() {
        return new UploadWSGroup();
    }

    @Override
    public void register(Channel channel) {
        super.register(channel);
        channel.writeAndFlush(Unpooled.wrappedBuffer("ack".getBytes()));

    }

    @Override
    public synchronized void onReceive(WebSocketFrame msg) {
        if (msg instanceof CloseWebSocketFrame) return;

        try {
            if (this.raf == null) {
                String sub = ((TextWebSocketFrame) msg).text();
                this.raf = new RandomAccessFile(
                        AbstractFile.get(Paths.get(sub)).get().getFile(),
                        "rw"
                );
                System.out.println(sub);
                this.channels.writeAndFlush(new TextWebSocketFrame("0"));
            } else {
                byte[] bytes = new byte[msg.content().readableBytes()];
                msg.content().duplicate().readBytes(bytes);
                this.raf.write(bytes);
                System.out.println(msg.content().toString());
                this.channels.writeAndFlush(new TextWebSocketFrame(this.raf.length() + ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
