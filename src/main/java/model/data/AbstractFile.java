package model.data;

import io.netty.handler.codec.http.HttpResponse;
import model.Assets;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public abstract class AbstractFile {
    protected static Logger logger = Logger.inst();

    protected File file;
    protected Path path;

    protected AbstractFile(Path path) {
        this.path = path;
        this.file = path.toFile();
    }

    public boolean isDirectory(){
        return file.isDirectory();
    }

    public abstract HttpResponse downloadResponse();

    public abstract HttpResponse uploadResponse();

    public static Optional<AbstractFile> get(Path sub) {
        Path abs = Assets.inst().getDataRoot().resolve(sub);
        return getFromAbsolutePath(abs);
    }

    public boolean delete() {
        return this.file.delete();
    }

    /*public static AbstractFile getOrCreate(Path sub) {
        Path abs = Assets.inst().getDataRoot().resolve(sub);
        return getFromAbsolutePath(abs).orElse(new RegularFile(abs));
    }*/

    public static Optional<AbstractFile> createAndGet(Path sub){
        Path abs = Assets.inst().getDataRoot().resolve(sub);
        try{
            abs.toFile().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getFromAbsolutePath(abs);
    }

    protected static Optional<AbstractFile> getFromAbsolutePath(Path abs) {
        File file = abs.toFile();
        if (!file.exists()) {
            return Optional.empty();
        } else if (file.isFile()) {
            return Optional.of(new RegularFile(abs));
        } else if (file.isDirectory()) {
            return Optional.of(new Directory(abs));
        } else {
            logger.error("Unknown file type: " + abs.toString());
            return Optional.empty();
        }
    }

    public abstract char type();

    public boolean exists(){
        return this.file.exists();
    }

    public FileInfo getInfo() {
        FileInfo info = new FileInfo();
        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            info.setName(file.getName())
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
}
