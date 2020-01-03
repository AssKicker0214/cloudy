import org.junit.Test;
import utils.MimeType;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MimeTypeTest {

    @Test
    public void testBlank(){
        assertEquals(MimeType.get(Paths.get("")), "application/octet-stream");
    }

    @Test
    public void testJS(){
        assertEquals(MimeType.get(Paths.get("/path/to/one.js")), "application/javascript");
    }

    @Test
    public void testCSS(){
        assertEquals(MimeType.get(Paths.get("/path/to/one.css")), "text/css");
    }
}
