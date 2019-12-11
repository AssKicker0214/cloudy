package routes.page;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import routes.Restful;
import routes.Route;
import routes.Routing;
import utils.StringUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Routing("/page/browse/?(|(?<=/).*)")
public class BrowsePage extends Route implements Restful {
    public static Path PAGE_ROOT;
    static {
        try {
            PAGE_ROOT = Paths.get(Objects.requireNonNull(BrowsePage.class.getClassLoader().getResource("page")).toURI());
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HttpResponse get(HttpRequest req, String... args) throws IOException {
        String file = StringUtil.isEmpty(args[0]) ? "" : args[0];
        Path path = PAGE_ROOT.resolve("browse.html");
        HttpResponse res = super.sendFile(path);
        res.headers().set("Content-Type", "text/html");
        return res;
    }
}
