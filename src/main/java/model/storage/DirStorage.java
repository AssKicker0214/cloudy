package model.storage;

import model.data.FileInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DirStorage {
    private Path abs;

    DirStorage(Path abs) {
        this.abs = abs;
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
}
