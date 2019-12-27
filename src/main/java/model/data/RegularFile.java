package model.data;

import io.netty.handler.codec.http.HttpResponse;
import response.BinaryDownload;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegularFile extends AbstractFile {

    RegularFile(Path path) {
        super(path);
    }

    @Override
    public HttpResponse downloadResponse() {
        return BinaryDownload.response(this.path);
    }

    @Override
    public HttpResponse uploadResponse() {
        return null;
    }

    @Override
    public char type() {
        return '-';
    }
}
