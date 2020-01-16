package utils;

import routes.page.BrowsePage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class Config extends Properties {
    private static Properties P;

    public static void refresh(){
        P = new Properties();
        try {
            P.load(Config.class.getClassLoader().getResourceAsStream("cloudy.conf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return P.getProperty(key);
    }

    public static int getInt(String key) {
        String value = P.getProperty(key);
        return Integer.parseInt(value);
    }

    public static int getIntOrDefault(String key, int defaultValue) {
        try {
            return getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Path dataRoot() {
        String root = P.getProperty("DATA_ROOT");
        return Paths.get(root);
    }

    public static Path pageRoot(){
        String root = P.getProperty("PAGE_ROOT");
        try {
           return Paths.get(Objects.requireNonNull(BrowsePage.class.getClassLoader().getResource(root)).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
