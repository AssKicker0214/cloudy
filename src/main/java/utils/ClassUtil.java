package utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassUtil {
    public static List<Class<?>> loadFrom(String pkgName) {
        ClassLoader cl = ClassUtil.class.getClassLoader();
        URL pkgDir = cl.getResource(pkgName);
        if (pkgDir == null) return new ArrayList<>();

        try {
            Path pkgPath = Paths.get(pkgDir.toURI());
            return Files.walk(pkgPath)
                    .map(pkgPath.getParent()::relativize)
                    .filter(p -> p.toString().endsWith(".class"))
                    .map(p -> p.toString().replace(File.separatorChar, '.'))
                    .map(c -> {
                        try {
                            return Class.forName(c.substring(0, c.length() - 6));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
