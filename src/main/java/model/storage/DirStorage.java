package model.storage;

import model.data.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DirStorage implements Deletable, Renamable {
    private Path abs;

    DirStorage(Path abs) {
        this.abs = abs;
    }

    public DirStorage init(){
        try {
            Files.createFile(this.abs.resolve(".cloudy"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public List<FileInfo> list() {
        File dir = abs.toFile();
        String[] children = dir.list();
        if (children == null) children = new String[]{};
        return Arrays.stream(children)
                .map(p -> this.abs.resolve(p))
                .map(Storage::getFileInfo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete() {
        boolean successful = false;
        try {
            successful = Files.walk(this.abs)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .allMatch(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return successful;
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
