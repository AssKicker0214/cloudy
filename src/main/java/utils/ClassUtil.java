package utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassUtil {
    public static List<Class<?>> loadFrom(String pkgName) {
        ClassLoader cl = ClassUtil.class.getClassLoader();
        URL pkgDir = cl.getResource(pkgName);
        if (pkgDir == null) return new ArrayList<>();

        FileSystem fs;
        try {
            Path pkgPath;
            URI uri = pkgDir.toURI();
            if (uri.getScheme().equals("jar")) {
                fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
                pkgPath = fs.getPath("/" + pkgName);
                fs.close();
            }else{
                pkgPath = Paths.get(pkgDir.toURI());
            }
            return Files.walk(pkgPath)
                    .map(pkgPath.getParent()::relativize)
                    .filter(p -> p.toString().endsWith(".class"))
                    .map(p -> p.toString().replaceAll("/+|\\\\+", "."))
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
