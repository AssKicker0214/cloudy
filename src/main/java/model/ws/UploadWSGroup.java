package model.ws;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import model.data.AbstractFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

public class UploadWSGroup extends WSGroup {
    private String abs;
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
                this.abs = ((TextWebSocketFrame) msg).text();
                System.out.println(abs);
                this.raf = new RandomAccessFile(
                        AbstractFile.get(Paths.get(abs.replaceFirst("/", ""))).get().getFile(),
                        "rw"
                );
                this.channels.writeAndFlush(new TextWebSocketFrame("0"));
            } else {
                byte[] bytes = new byte[msg.content().readableBytes()];
                msg.content().duplicate().readBytes(bytes);
                if (bytes.length == 0) {
                    this.raf.close();
                    ControlWSGroup.inst().sendToAll(new ControlMessage(
                            "REFRESH_DIRECTORY",
                            this.abs.substring(0, this.abs.lastIndexOf('/')+1)
                    ));
                    System.out.println("upload done");
                } else {
                    this.raf.write(bytes);
                    System.out.println("+" + bytes.length + " " + this.raf.length());
                    this.channels.writeAndFlush(new TextWebSocketFrame(this.raf.length() + ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleException(ChannelHandlerContext ctx, Throwable cause) {
        if (this.raf != null) {
            try {
                this.raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
