package model.data;

import io.netty.handler.codec.http.HttpResponse;
import response.ApplicationJson;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Directory extends AbstractFile {

    Directory(Path path) {
        super(path);
    }

    @Override
    public HttpResponse downloadResponse() {
        String[] children = this.file.list() == null ? new String[]{} : this.file.list();
        assert children != null;
        List<FileInfo> list = Arrays.stream(children)
                .map(p -> this.path.resolve(p))
                .map(AbstractFile::getFromAbsolutePath)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(AbstractFile::getInfo)
                .collect(Collectors.toList());
        return ApplicationJson.response(list);
    }

    @Override
    public HttpResponse uploadResponse() {
        return null;
    }

    @Override
    public char type() {
        return 'd';
    }
}
