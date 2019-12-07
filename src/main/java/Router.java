import io.netty.handler.codec.http.HttpRequest;
import routes.BaseRoute;
import routes.Route;
import utils.ClassUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

    private static Router instance = new Router();
    private Map<Pattern, BaseRoute> routeMap;

    void dispatch(HttpRequest req) {
        for (Pattern ptn : routeMap.keySet()) {
            Matcher m = ptn.matcher(req.uri());
//            if (m.find()) {
//                routeMap.get(ptn)
//                break;
//            }
        }
    }

    static Router newInstance() {
        return instance;
    }

    private Router() {
        loadRoutes();
        printRoutes();
    }

    private void loadRoutes() {
        Map<Pattern, BaseRoute> map = new HashMap<>();
        try {
            for (Class<?> clazz : ClassUtil.loadFrom("routes")) {
                Route a = clazz.getAnnotation(Route.class);
                if (a == null) continue;
                String[] values = a.value();
                BaseRoute route = (BaseRoute) clazz.newInstance();
                for (String value : values) {
                    map.put(Pattern.compile(value), route);
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.routeMap = Collections.unmodifiableMap(map);
    }

    public void printRoutes() {
        System.out.println("Routes:");
        for (Map.Entry<Pattern, BaseRoute> entry : this.routeMap.entrySet()) {
            System.out.println(entry.getKey()+"->"+entry.getValue());
        }
    }
}
