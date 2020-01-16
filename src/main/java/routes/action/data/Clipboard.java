package routes.action.data;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import model.ws.ControlMessage;
import model.ws.ControlWSGroup;
import response.ApplicationJson;
import response.NotFound;
import response.OK;
import routes.DefaultEndpoint;
import routes.Restful;
import routes.Routing;
import utils.Config;
import utils.UrlUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("unused")
@Routing("/clipboard(s|/.*)")
public class Clipboard extends DefaultEndpoint implements Restful {
    private static ConcurrentLinkedQueue<ClipboardRecord> CACHE = new ConcurrentLinkedQueue<>();
    public static final int MAX_CLIPBOARD_RECORD_NUM = Config.getIntOrDefault("MAX_CLIPBOARD_RECORD_NUM", 5);

    @Override
    public HttpResponse get(FullHttpRequest req, String... args) throws IOException {
        if (args[0].equals("s")) {
//            int recentN = UrlUtil.queryString(req.uri(), "")
            return ApplicationJson.response(CACHE.toArray(new ClipboardRecord[]{}));
        }else{
            long id = Long.parseLong(args[0].substring(1));
            for (ClipboardRecord record : CACHE) {
                if (record.id == id) {
                    return ApplicationJson.response(record);
                }
            }
        }
        return NotFound.response(args[0]);
    }

    @Override
    public HttpResponse post(FullHttpRequest req, String... args) {
        if (!args[0].equals("s")) return NotFound.response(args[0]);

        String text = req.content().toString(StandardCharsets.UTF_8);
        ClipboardRecord record = new ClipboardRecord(text);
        CACHE.add(record);
        while (CACHE.size() > MAX_CLIPBOARD_RECORD_NUM) {
            CACHE.poll();
        }
        ControlWSGroup.inst().sendToAll(new ControlMessage(
                "REFRESH_CLIPBOARD"
        ));
        return OK.response("");
    }

    @Override
    public HttpResponse delete(FullHttpRequest req, String... args) {
        return super.delete(req, args);
    }

    public static class ClipboardRecord{
        private long id;
        private String text;

        ClipboardRecord(String text) {
            this.id = new Date().getTime();
            this.text = text;
        }

        public long getId() {
            return this.id;
        }

        public String getText() {
            return this.text;
        }

    }
}
