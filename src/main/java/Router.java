import io.netty.handler.codec.http.*;
import routes.Route;
import routes.Routing;
import utils.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

    private static Router instance = new Router();
    private Map<Pattern, Route> routeMap;

    HttpResponse dispatch(HttpRequest req) {
        for (Pattern ptn : routeMap.keySet()) {
            Matcher m = ptn.matcher(req.uri());
            if (m.find()) {
                Route route = routeMap.get(ptn);
                String[] args = new String[m.groupCount()];
                for (int i = 0; i < m.groupCount(); i++)   args[i] = m.group(i+1);
                return route.process(req, args);
            }
        }
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }

    static Router newInstance() {
        return instance;
    }

    private Router() {
        loadRoutes();
        printRoutes();
    }

    private void loadRoutes() {
        Map<Pattern, Route> map = new HashMap<>();
        try {
            for (Class<?> clazz : ClassUtil.loadFrom("routes")) {
                Routing a = clazz.getAnnotation(Routing.class);
                if (a == null) continue;
                String value = a.value();
                Route route = (Route) clazz.getDeclaredConstructor().newInstance();
                map.put(Pattern.compile(value), route);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.routeMap = Collections.unmodifiableMap(map);
    }

    public void printRoutes() {
        System.out.println("Routes:");
        for (Map.Entry<Pattern, Route> entry : this.routeMap.entrySet()) {
            System.out.println(entry.getKey()+"->"+entry.getValue());
        }
    }
}
