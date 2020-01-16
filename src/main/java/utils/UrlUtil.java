package utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class UrlUtil {
    public static Optional<String> queryString(String uri, String key) {
        if(!uri.contains("?"))  return Optional.empty();

        String[] queries = uri.substring(uri.indexOf('?')+1).split("&");
        return Arrays.stream(queries)
                .filter(kv -> kv.startsWith(key + "="))
                .map(kv -> kv.substring(kv.indexOf('=') + 1))
                .findFirst();
    }
}
