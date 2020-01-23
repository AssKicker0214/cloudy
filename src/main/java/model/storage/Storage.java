package model.storage;

import model.data.FileInfo;
import utils.Config;
import utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public class Storage {
    public static final char TYPE_DIRECTORY = 'd';
    public static final char TYPE_FILE = '-';

    public static Path getRealPath(String path) {
        Path root = Config.dataRoot();
        String relative = path.replaceAll("^/", "");
        return root.resolve(relative);
    }

    /**
     * THIS METHOD CAN BE USED TO CHECK WHETHER A FILE EXISTS
     * if the file (targeted by path) is a regular file or directory (hidden files/directories excluded),
     * return its type;
     *
     * @param path path to file
     * @return file type, or an empty optional if the file not exists or visible to user
     */
    public static Optional<Character> type(String path) {
        Path realPath = getRealPath(path);
        File file = realPath.toFile();

        // TODO filter <meta file>
        if (!file.exists()) return Optional.empty();
        if (file.isDirectory()) return Optional.of(TYPE_DIRECTORY);
        if (file.isFile()) return Optional.of(TYPE_FILE);

        return Optional.empty();
    }

    public static Optional<String> fileHash(String path) {
        Path realPath = getRealPath(path);
        Optional<Character> opt = type(path);

        // only regular file has a hash
        if(!opt.isPresent() || opt.get() != TYPE_FILE)    return Optional.empty();

        return Optional.ofNullable(FileUtil.sha256(realPath.toFile()));
    }

    public static long fileSize(String path) {
        Path realPath = getRealPath(path);
        return realPath.toFile().length();
    }

    public static DirStorage createDirectory(String path) {
        Path abs = getRealPath(path);
        try {
            Files.createDirectories(abs);
            return new DirStorage(abs).init();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DirStorage getDirectory(String path) {
        if (type(path).orElse('?') != TYPE_DIRECTORY) return null;

        Path abs = getRealPath(path);
        return new DirStorage(abs);
    }

    public static FileStorage createFile(String path) {
        Path abs = getRealPath(path);
        try {
            Files.createFile(abs);
            return new FileStorage(abs);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FileStorage createFile(String directory, String filename) {
        String path = (directory + "/" + filename).replaceAll("//", "/");
        return createFile(path);
    }

    public static FileStorage getFile(String path) {
        if (type(path).orElse('?') != TYPE_FILE) return null;

        Path abs = getRealPath(path);
        return new FileStorage(abs);
    }

    public static FileInfo getFileInfo(Path abs) {
        FileInfo info = new FileInfo();
        try {
            BasicFileAttributes attr = Files.readAttributes(abs, BasicFileAttributes.class);
            info.setName(abs.getFileName().toString())
                    .setType(attr.isDirectory() ? 'd' : '-')
                    .setCreated(attr.creationTime().toMillis())
                    .setModified(attr.lastModifiedTime().toMillis())
                    .setAccessed(attr.lastAccessTime().toMillis())
                    .setSize(attr.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static Optional<Deletable> toDelete(String path) {
        return toOperate(path);
    }

    public static Optional<Renamable> toRename(String path) {
        return toOperate(path);
    }

    @SuppressWarnings("unchecked")
    private static <T> Optional<T> toOperate(String path) {
        Optional<Character> opt = Storage.type(path);
        if (!opt.isPresent()) return Optional.empty();

        if (opt.get() == TYPE_DIRECTORY) return Optional.of((T) new DirStorage(getRealPath(path)));
        if (opt.get() == TYPE_FILE) return Optional.of((T) new FileStorage(getRealPath(path)));

        return Optional.empty();
    }
}
