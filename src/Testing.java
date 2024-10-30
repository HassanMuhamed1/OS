import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class Testing {
    private static final String INITIAL_DIR = System.getProperty("user.dir");
    private CLI cli;

    @Test
    void testPwd() {
        // Check if the current working directory is correct
        String expectedPath = INITIAL_DIR;
        assertEquals(expectedPath, cli.pwd(), "PWD command failed.");
    }

    @Test
    void testChangeDirectory() {
        // Create a temporary directory for testing
        String tempDirName = "testDir";
        File tempDir = new File(CLI.currentDirectory, tempDirName);
        tempDir.mkdir();

        // Change to the new directory
        cli.changeDirectory(new String[]{"cd", tempDirName});
        assertEquals(tempDir.getAbsolutePath(), cli.pwd(), "CD command failed.");

        // Cleanup: Return to the original directory and delete the temp directory
        cli.changeDirectory(new String[]{"cd", ".."});
        tempDir.delete();
    }

    @Test
    void testMvRenameFile() throws IOException {
        // Create a temporary file to test renaming
        File srcFile = new File(CLI.currentDirectory, "file1.txt");
        File destFile = new File(CLI.currentDirectory, "fileRenamed.txt");
        srcFile.createNewFile();  // Create the source file

        // Perform the mv (rename) operation
        cli.mv(new String[]{"mv", "file1.txt", "fileRenamed.txt"});

        // Verify that the original file no longer exists and the renamed one does
        assertFalse(srcFile.exists(), "Source file still exists after rename.");
        assertTrue(destFile.exists(), "Renamed file not found.");

        // Cleanup
        destFile.delete();
    }

    @Test
    void testMvMoveFileToDirectory() throws IOException {
        // Create a temporary file and directory for testing
        File srcFile = new File(CLI.currentDirectory, "file2.txt");
        File destDir = new File(CLI.currentDirectory, "testDir");
        srcFile.createNewFile();
        destDir.mkdir();

        // Perform the mv (move) operation to the directory
        cli.mv(new String[]{"mv", "file2.txt", "testDir"});

        // Verify that the file was moved to the destination directory
        File movedFile = new File(destDir, "file2.txt");
        assertTrue(movedFile.exists(), "File not moved to the directory.");

        // Cleanup
        movedFile.delete();
        destDir.delete();
    }
    // @Test
    // public void testpwd(){
    //     CLI cli = new CLI();
    //     String expectString = System.getProperty("user.dir");
    //     String actual = cli.pwd();
    //     Assert.assertEquals(expectString, actual);
    // }


    // @Test
    // public void testcd() {
    //     CLI cli = new CLI();
    
    //     String targetDirectory = "bin";
    //     File expectedDir = new File(System.getProperty("user.dir"), targetDirectory);

    //     cli.changeDirectory(new String[]{"cd", targetDirectory});

    //     assertEquals(expectedDir.getAbsolutePath(), cli.pwd());
    // }
}
