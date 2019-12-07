package routes;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


@SuppressWarnings("Unused")
@Routing("/static/(.+)")
public class Static extends Route {
    private static Path STATIC_ROOT;

    static {
        try {
            // TODO make `static` configurable
            STATIC_ROOT = Paths.get(Objects.requireNonNull(Static.class.getClassLoader().getResource("static")).toURI());
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected HttpResponse get(HttpRequest req, String... args) throws IOException {
        String resourcePath = args[0];
        Path targetPath = STATIC_ROOT.resolve(resourcePath);
        if (targetPath.toFile().isFile()) {
//            byte[] contents = "test".getBytes();
            byte[] contents = Files.readAllBytes(targetPath);
            HttpResponse res = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(contents)
            );
            res.headers().set("Content-Length", contents.length);
            return res;
        }else{
            return new response.NotFound();
        }
    }
}
