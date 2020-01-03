import org.junit.Test;
import utils.MimeType;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class MimeTypeTest {

//    @BeforeAll
//    public void before() {
//        assertTrue(MimeType.getAllMapping().entrySet().size() > 0);
//    }

    @Test
    public void testWithoutSuffix(){
        assertEquals(MimeType.get(Paths.get("path/to/target")), "application/octet-stream");
    }

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
