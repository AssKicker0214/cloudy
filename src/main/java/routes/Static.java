package routes;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import utils.MimeType;
import utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("Unused")
@Routing("/static/(.+)")
public class Static extends DefaultEndpoint implements Restful {
    private static Path STATIC_ROOT;

    static {
        try {
            STATIC_ROOT = Paths.get(Objects.requireNonNull(Static.class.getClassLoader().getResource("static")).toURI());
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HttpResponse get(FullHttpRequest req, String... args) throws IOException {
        String resourcePath = args[0];
        Path targetPath = STATIC_ROOT.resolve(resourcePath);
        File target = targetPath.toFile();

        if (!target.isFile()) return new response.NotFound();

        String ifModifiedSince = req.headers().get("If-Modified-Since");
        SimpleDateFormat dateFormat = new SimpleDateFormat(DefaultEndpoint.HTTP_DATE_FORMAT, Locale.US);
        if (!StringUtil.isEmpty(ifModifiedSince)) {
            try {
                Date ifModifiedSinceDate = dateFormat.parse(ifModifiedSince);
                long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
                long fileLastModifiedSeconds = target.lastModified() / 1000;
                if (ifModifiedSinceDateSeconds >= fileLastModifiedSeconds) {
                    return new response.NotModified();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        byte[] contents = Files.readAllBytes(targetPath);
        HttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(contents)
        );
        res.headers().set("Content-Length", contents.length);
        res.headers().set("Content-Type", MimeType.get(targetPath));
        res.headers().set("Cache-Control", "private, max-age=84600");
        res.headers().set("Last-Modified", dateFormat.format(new Date(target.lastModified())));
        return res;
    }

}
