import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

import java.net.URLDecoder;
import java.util.List;

public class URIDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest req, List<Object> out) throws Exception {
        String percentageEncoded = req.uri();
        String percentageDecoded = URLDecoder.decode(percentageEncoded, "utf-8");
        req.setUri(percentageDecoded);
        out.add(req.retain());
    }
}
