import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class Testing {
    private static final String INITIAL_DIR = System.getProperty("user.dir");
    private CLI cli;
    
    @Test
    void testPwd() {
        // Check if the current working directory is correct
        String expectedPath = System.getProperty("user.dir");
        assertEquals(expectedPath, cli.pwd(), "PWD command failed.");
    }

    @Test
    void testcd() {
        // Create a temporary directory for testing
        String tempDirName = "src";
        File tempDir = new File(CLI.currentDirectory, tempDirName);

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
    @Test
    public void testMkdir() {
        CLI cli = new CLI();
        String newDirName = "newDir";
        File newDir = new File(System.getProperty("user.dir"), newDirName);

        // Create the directory
        cli.makeDirectory(newDirName);
        assertTrue(newDir.exists() && newDir.isDirectory(), "Directory should be created");

        // Cleanup (delete the directory after test)
        newDir.delete();
    }
    @Test
    void testTouch() throws IOException{
        CLI cli = new CLI();
        String fileName="testFile.txt";
        File testFile= new File(CLI.currentDirectory,fileName);
        cli.createFile(fileName);
        assertTrue(testFile.exists(), "File was not created by the touch command.");//verifying
        testFile.delete();
    }
    @Test
    void testRemovefile() throws IOException{
        CLI cli = new CLI();
        String fileName="testFile.txt";
        File testFile= new File(System.getProperty("user.dir"),fileName);
        Files.createFile(testFile.toPath());
        assertTrue(testFile.exists(), "File was not created.");

        // Now remove the file using the method under test
        cli.removeFile(fileName);

        // Verify that the file has been deleted
        assertFalse(testFile.exists(), "File was not deleted.");
    }

    @Test
    public void testRmdir() {
        CLI cli = new CLI();
        String dirToDelete = "dirToRemove";
        File dir = new File(System.getProperty("user.dir"), dirToDelete);

        // Ensure the directory is created first
        cli.makeDirectory(dirToDelete);
        assertTrue(dir.exists(), "Directory should exist before deletion");

        // Now remove it using rmdir
        cli.removeDirectory(dirToDelete);
        assertFalse(dir.exists(), "Directory should be removed");
    }
    @Test
    void testPipe(){
        File currFile = new File(INITIAL_DIR);
        File [] files = currFile.listFiles();
        String [] command = new String[]{"ls","sort"};
        cli.pipe(command);
    }

    @Test
    void testListFiles() throws IOException {
        File hiddenFile = new File(CLI.currentDirectory, ".hiddenFile.txt");
        File visibleFile = new File(CLI.currentDirectory, "fileVisible.txt");
        hiddenFile.createNewFile();
        visibleFile.createNewFile();
        System.out.println("Testing listFiles:");
        CLI.listFiles();
        hiddenFile.delete();
        visibleFile.delete();
    }

    @Test
    void testListFilesAll() throws IOException {
        File hiddenFile = new File(CLI.currentDirectory, ".hiddenFile.txt");
        File visibleFile = new File(CLI.currentDirectory, "fileVisible.txt");
        hiddenFile.createNewFile();
        visibleFile.createNewFile();
        System.out.println("Testing listFilesAll:");
        CLI.listFilesAll();
        hiddenFile.delete();
        visibleFile.delete();
    }

    @Test
    void testListFilesReverse() throws IOException {
        File fileA = new File(CLI.currentDirectory, "aFile.txt");
        File fileB = new File(CLI.currentDirectory, "bFile.txt");
        fileA.createNewFile();
        fileB.createNewFile();
        System.out.println("Testing listFilesReverse:");
        CLI.listFilesReverse();
        fileA.delete();
        fileB.delete();
    }

    @Test
    void testRedirectOutputToFile() throws IOException {
        String filename = "output.txt";
        String command = "ls > " + filename;

        try {
            // Run the command
            cli.handleComplexCommand(command);

            // Verify that the file was created and contains the expected content
            File outputFile = new File(CLI.currentDirectory, filename);
            assertTrue(outputFile.exists(), "Output file should be created.");

            // Read content from the file and verify it matches expected directory listing
            String[] expectedFiles = CLI.currentDirectory.list();
            String actualContent = new String(Files.readAllBytes(outputFile.toPath()));
            for (String expectedFile : expectedFiles) {
                assertTrue(actualContent.contains(expectedFile), "File listing does not match expected content.");
            }
        } finally {
            // Cleanup
            new File(CLI.currentDirectory, filename).delete();
        }
    }

    @Test
    void testAppendOutputToFile() throws IOException {
        String filename = "output_append.txt";
        String command = "pwd >> " + filename;
    
        // Create the file initially with some content
        Files.write(Paths.get(CLI.currentDirectory.getAbsolutePath(), filename), "Initial Content\n".getBytes());
    
        try {
            // Run the command
            cli.handleComplexCommand(command);
    
            // Verify that the file was created and contains the expected content
            File outputFile = new File(CLI.currentDirectory, filename);
            assertTrue(outputFile.exists(), "Output file should be created.");
    
            // Read content from the file and verify it matches expected content
            String actualContent = new String(Files.readAllBytes(outputFile.toPath()));
            String expectedContent = "Initial Content\n" + cli.pwd() + System.lineSeparator();
            assertEquals(expectedContent, actualContent, "File content does not match expected append content.");
        } finally {
            // Cleanup
            new File(CLI.currentDirectory, filename).delete();
        }
    }
}
