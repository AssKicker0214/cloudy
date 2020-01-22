import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import utils.Config;

public class SimpleHTTPServer {
    private int port;

    private SimpleHTTPServer(int port) {
        this.port = port;
    }

    private void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new HttpRequestDecoder())
                                    .addLast("HTTPAggregator", new HttpObjectAggregator(1048576))
                                    .addLast(new URIDecoder())
                                    .addLast(new HttpResponseEncoder())
//                                    .addLast(new ChunkedWriteHandler())
                                    .addLast("HTTPHandler", new HTTPHandler());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind("0.0.0.0", port).sync();
            System.out.println("Listening on 0.0.0.0:"+port);
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        Config.refresh();
        System.out.println("COMMIT_ID: "+System.getenv("COMMIT_ID"));
        System.out.println("COMMIT_URL: "+System.getenv("COMMIT_URL"));
        int port = Config.getIntOrDefault("SERVER_PORT", 80);
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new SimpleHTTPServer(port).run();
    }
}
