import io.netty.handler.codec.http.*;
import routes.Restful;
import routes.Route;
import routes.Routing;
import utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

    private static Router instance = new Router();
    private Map<Pattern, Restful> routeMap;

    HttpResponse dispatch(HttpRequest req) throws IOException {
        for (Pattern ptn : routeMap.keySet()) {
            Matcher urlMather = ptn.matcher(req.uri());
            if (!urlMather.find()) continue;

            HttpMethod method = req.method();
            String[] args = new String[urlMather.groupCount()];
            for (int i = 0; i < urlMather.groupCount(); i++) args[i] = urlMather.group(i + 1);
            if (method.equals(HttpMethod.GET)) {
                Restful route = routeMap.get(ptn);
                return route.get(req, args);
            }
        }
        return new response.NotFound();
    }

    static Router newInstance() {
        return instance;
    }

    private Router() {
        loadRoutes();
        printRoutes();
    }

    private void loadRoutes() {
        Map<Pattern, Restful> map = new HashMap<>();
        try {
            for (Class<?> clazz : ClassUtil.loadFrom("routes")) {
                Routing a = clazz.getAnnotation(Routing.class);
                if (a == null) continue;
                String value = a.value();
                Restful route = (Restful) clazz.getDeclaredConstructor().newInstance();
                map.put(Pattern.compile(value), route);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.routeMap = Collections.unmodifiableMap(map);
    }

    public void printRoutes() {
        System.out.println("Routes:");
        for (Map.Entry<Pattern, Restful> entry : this.routeMap.entrySet()) {
            System.out.println(entry.getKey() + "->" + entry.getValue());
        }
    }
}
