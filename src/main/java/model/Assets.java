package model;

import routes.page.BrowsePage;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Assets {
    private static final Assets self = new Assets();

    private Path pageRoot;
    private Path dataRoot;

    private Assets() {
        this.load();
    }

    public void load() {
        try {
            pageRoot = Paths.get(Objects.requireNonNull(BrowsePage.class.getClassLoader().getResource("page")).toURI());
            dataRoot = Paths.get(".");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Path getPageRoot() {
        return this.pageRoot;
    }

    public Path getDataRoot(){
        return this.dataRoot;
    }

    public static Assets inst() {
        return self;
    }
}
