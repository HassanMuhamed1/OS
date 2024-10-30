import static org.junit.Assert.assertEquals;
import java.io.File;
import org.junit.*;

public class Testing {
    
    @Test
    public void testpwd(){
        CLI cli = new CLI();
        String expectString = System.getProperty("user.dir");
        String actual = cli.pwd();
        Assert.assertEquals(expectString, actual);
    }


    @Test
    public void testcd() {
        CLI cli = new CLI();
    
        String targetDirectory = "bin";
        File expectedDir = new File(System.getProperty("user.dir"), targetDirectory);

        cli.changeDirectory(new String[]{"cd", targetDirectory});

        assertEquals(expectedDir.getAbsolutePath(), cli.pwd());
    }
}
