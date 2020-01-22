package model.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorage implements Deletable, Renamable {
    private Path abs;

    FileStorage(Path abs) {
        this.abs = abs;
    }

    @Override
    public boolean delete() {
        return abs.toFile().delete();
    }

    @Override
    public boolean rename(String to) {
        try {
            Files.move(this.abs, this.abs.resolveSibling(to));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
