import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.*;
public class CLI {
    protected static File currentDirectory = new File(System.getProperty("user.dir"));
    private static File getRoot(){
        File currentRoot = new File(System.getProperty("user.dir"));
        while (currentRoot.getParent() != null) {
            currentRoot = new File(currentRoot.getParent());
        }
        return currentRoot;
    }
    public static void printHelp() {
        System.out.println("Available Commands:");
        System.out.println("pwd          - Print the current directory");
        System.out.println("ls           - List files in the current directory");
        System.out.println("cd <dir>     - Change directory");
        System.out.println("mkdir <dir>  - Create a new directory");
        System.out.println("exit         - Exit the CLI");
        System.out.println("help         - Show available commands");
    }

    protected static void listFiles() {
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("No files found.");
        }
    }

    protected static void changeDirectory(String[] command) {
        // cd with no arguments
        if (command.length == 1) {
            currentDirectory = new File(System.getProperty("user.home"));
            return;
        }
    
        // cd /
        if (command.length == 2 && command[1].equals("/")) {
            File rootDirectory = getRoot();
            currentDirectory = rootDirectory;
            return;
        }
    
        // cd ~ (Home Directory)
        if (command.length == 2 && command[1].equals("~")) {
            currentDirectory = new File(System.getProperty("user.home"));
            return;
        }
    
        // cd .. (Parent Directory)
        if (command.length == 2 && command[1].equals("..")) {
            String parentPath = currentDirectory.getParent();
            if (parentPath != null) {
                currentDirectory = new File(parentPath);
            } else {
                System.out.println("You are in the root directory, can't go back further!");
            }
            return;
        }
    
        // cd [directory_path] or cd dir_1/dir_2/dir_3
    if (command.length == 2) {
        String targetPath = command[1].trim();
        File newDir;

        // Handle absolute vs. relative paths
        if (isAbsolutePath(targetPath)) {
            newDir = new File(targetPath);  // Absolute path
        } else {
            newDir = resolveRelativePath(targetPath);  // Relative path
        }

        // Validate the final resolved directory
        if (newDir != null && newDir.exists() && newDir.isDirectory()) {
            currentDirectory = newDir.getAbsoluteFile();
        } else {
            System.out.println("Invalid directory: " + targetPath);
        }
    }
}
    private static boolean isAbsolutePath(String path) {
        return path.startsWith(File.separator) || path.matches("^[A-Z]:.*");
    }
    
    // Helper method to resolve relative paths step-by-step with case-sensitive checks
    private static File resolveRelativePath(String path) {
        String[] parts = path.split("[/\\\\]");  // Support both / and \
        File dir = currentDirectory;
    
        for (String part : parts) {
            if (part.isEmpty()) continue;  // Skip empty parts (e.g., from trailing slashes)
    
            File[] matchingDirs = dir.listFiles(File::isDirectory);
            if (matchingDirs == null) {
                System.out.println("Error: Unable to access " + dir.getAbsolutePath());
                return null;
            }
    
            // Find a directory with the exact case-sensitive match
            List<File> matches = Arrays.stream(matchingDirs)
                    .filter(f -> f.getName().equals(part))
                    .toList();
    
            if (matches.isEmpty()) {
                System.out.println("Invalid directory: " + part);
                return null;
            }
    
            dir = matches.get(0);  // Move to the next level
        }
        return dir;
    }

    
    protected static void makeDirectory(String dirName) {
        File newDir = new File(currentDirectory, dirName);
        if (newDir.exists()) {
            System.out.println("Directory already exists: " + dirName);
        } else if (newDir.mkdir()) {
            System.out.println("Directory created: " + dirName);
        } else {
            System.out.println("Failed to create directory: " + dirName);
        }
    }

    protected static String pwd(){
        return currentDirectory.getAbsolutePath();
    }


    protected static void mv(String[] command) throws IOException, NoSuchFileException {
        // Check if there are at least two arguments (one or more source + one destination)
        if (command.length < 3) {
            throw new IllegalArgumentException("Usage: mv <source1> <source2> ... <destination>");
        }
    
        // The last argument is the destination (either a new file name or a directory)
        File dst = makeAbsolute(command[command.length - 1]);
    
        // Handle renaming if there's only one source file
        if (command.length == 3) {
            File src = makeAbsolute(command[1]);
    
            if (!src.exists()) {
                throw new NoSuchFileException(src.getAbsolutePath(), null, "does not exist.");
            }
    
            // If destination is a directory, move the source file into it
            if (dst.isDirectory()) {
                Files.move(
                    src.toPath(),
                    dst.toPath().resolve(src.getName()),  // Move into the directory
                    StandardCopyOption.REPLACE_EXISTING
                );
            } else {
                // Rename the source file to the destination file name
                Files.move(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return;
        }
    
        // If there are multiple source files, ensure the destination is a directory
        if (!dst.exists() || !dst.isDirectory()) {
            throw new IOException("Destination must be an existing directory: " + dst.getAbsolutePath());
        }
    
        // Loop through all source files and move them to the destination directory
        for (int i = 1; i < command.length - 1; i++) {
            File src = makeAbsolute(command[i]);
    
            if (!src.exists()) {
                throw new NoSuchFileException(src.getAbsolutePath(), null, "does not exist.");
            }
    
            // Move the source file into the destination directory
            Files.move(
                src.toPath(),
                dst.toPath().resolve(src.getName()),  // Place inside the destination
                StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
    
    public static File makeAbsolute(String sourcePath){
        File f = new File(sourcePath);
        if(!f.isAbsolute()) {
            f = new File(currentDirectory.getAbsolutePath(), sourcePath);
        }
        return f.getAbsoluteFile();
    }

}
