package utils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MimeType {
    private static Map<String, String> MAP;

    static {
        MAP = new HashMap<>();
        InputStream is = MimeType.class.getClassLoader().getResourceAsStream("mime.type");
        if(is == null) throw new RuntimeException("Cannot find file: mime.type");
        Scanner s = new Scanner(is).useDelimiter("\n");
        while (s.hasNext()) {
            String ln = s.next();
            String[] segments = ln.split("\\s+");
            for (int i = 1; i < segments.length; i++) {
                MAP.put(segments[i], segments[0]);
            }
        }
    }

    public static Map<String, String> getAllMapping() {
        return MAP;
    }

    public static String getBySuffix(String suffix) {
        return MAP.getOrDefault(suffix.toLowerCase(), "application/octet-stream");
    }

    public static String get(String filename) {
        int lastDotPos = filename.lastIndexOf('.');
        String suffix = lastDotPos > 0 ? filename.substring(lastDotPos+1) : "";
        return getBySuffix(suffix);
    }

    public static String get(Path path) {
        String filename = path.getFileName().toString();
        return get(filename);
    }
}
