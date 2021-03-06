package routes;

import io.netty.handler.codec.http.HttpMethod;
import utils.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestCall {
    private static Map<Pattern, Class<Restful>> MAP;
    Restful endpoint;
    String[] args;

    static {
        Map<Pattern, Class<Restful>> map = new HashMap<>();
        for (Class<?> clazz : ClassUtil.loadFrom("routes")) {
            Routing a = clazz.getAnnotation(Routing.class);
            if (a == null) continue;
            String value = a.value();
            if(!value.startsWith("^"))  value = "^" + value;
            if(!value.endsWith("$"))    value = value + "$";
            //noinspection unchecked
            map.put(Pattern.compile(value), (Class<Restful>) clazz);
        }
        MAP = Collections.unmodifiableMap(map);
    }

    private RestCall(Restful endpoint, String[] args) {
        this.endpoint = endpoint;
        this.args = args;
    }

    public static RestCall parseURI(String uri) {
        for (Pattern ptn : MAP.keySet()) {
            Matcher m = ptn.matcher(uri);
            if (!m.find()) continue;

            String[] args = new String[m.groupCount()];
            for (int i = 0; i < m.groupCount(); i++) args[i] = m.group(i + 1);

            Class<Restful> clazz = MAP.get(ptn);
            try {
                Restful endpoint = clazz.newInstance();
                return new RestCall(endpoint, args);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Restful getEndpoint() {
        return endpoint;
    }

    public String[] getArgs() {
        return args;
    }
}
