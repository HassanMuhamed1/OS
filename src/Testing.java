import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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

    @Test
    public void testMkdir() {
        CLI cli = new CLI();
        String newDirName = "newDir";
        File newDir = new File(System.getProperty("user.dir"), newDirName);

        // Create the directory
        cli.makeDirectory(newDirName);
        assertTrue("Directory should be created", newDir.exists() && newDir.isDirectory());

        // Cleanup (delete the directory after test)
        newDir.delete();
    }

    @Test
    public void testRmdir() {
        CLI cli = new CLI();
        String dirToDelete = "dirToRemove";
        File dir = new File(System.getProperty("user.dir"), dirToDelete);

        // Ensure the directory is created first
        cli.makeDirectory(dirToDelete);
        assertTrue("Directory should exist before deletion", dir.exists());

        // Now remove it using rmdir
        cli.removeDirectory(dirToDelete);
        assertFalse("Directory should be removed", dir.exists());
    }
}
