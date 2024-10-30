import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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
        // if (command.length == 2) {
        //     File newDir = new File(command[1].startsWith(File.separator) ? 
        //                            command[1] : 
        //                            new File(currentDirectory, command[1]).getPath());
        //     if (newDir.exists() && newDir.isDirectory()&& newDir.isFile()) {
        //         currentDirectory = newDir;
        //     } else {
        //         System.out.println("Invalid directory: " + command[1]);
        //     }
        // }
    //     if (command.length == 2) {
    //         // Create a new File object with either an absolute or relative path
    //         File newDir = new File(command[1].startsWith(File.separator) ? 
    //                                command[1] : 
    //                                new File(currentDirectory, command[1]).getPath());
        
    //         // Check if the new path exists
    //         // if (newDir.exists()) {
    //         //     // Check if it is a directory
    //         //     if (newDir.isDirectory()) {
    //         //         currentDirectory = newDir.getAbsoluteFile();  // Change the working directory
    //         //     } else {
    //         //         System.out.println("Error: The path is a file, not a directory.");
    //         //     }
    //         // } else {
    //         //     System.out.println("Invalid directory: " + command[1]);
    //         // }
    //         File[] directories = currentDirectory.listFiles(File::isDirectory);
    // if (directories != null) {
    //     String targetDir = command[1].trim();
    //     boolean found = Arrays.stream(directories)
    //             .anyMatch(dir -> dir.getName().equals(targetDir));

    //     if (found) {
    //         // If directory exists and matches case-sensitively, change directory
    //         currentDirectory = new File(currentDirectory, targetDir).getAbsoluteFile();
    //     } else {
    //         System.out.println("Invalid directory: " + targetDir);
    //     }
    // } else {
    //     System.out.println("Error: Unable to list directories.");
    // }
    //     }
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
        }
        else if (newDir.mkdir()) {
            System.out.println("Directory created: " + dirName);
        }
        else {
            System.out.println("Failed to create directory: " + dirName);
        }
    }

    protected static void removeDirectory(String dirName) {
        File newDir = new File(currentDirectory, dirName);
        if (!newDir.exists()) {
            System.out.println("Directory Does not exists: " + dirName);
        } else if (newDir.delete()) {
            System.out.println("Directory Removed.");
        } else {
            System.out.println("Failed to remove " + dirName+ ":Directory not empty ");
        }
    }

    protected static String pwd(){
        return currentDirectory.getAbsolutePath();
    }

    protected static void mv(String []command)throws IOException,NoSuchFileException{
        File src = makeAbsolute(command[1]);
        File dst = makeAbsolute(command[2]);
        if(!src.exists()) {
            throw new NoSuchFileException(src.getAbsolutePath(), null, "does not exist.");
        }
        if(dst.isFile()){
            throw new IOException("Can't move into file.");
        }

        if(!dst.exists()){ //renaming
            Files.move(src.toPath(),src.toPath().resolveSibling(dst.getName()));
        }
        else{
            for(int idx = 0; idx < command.length-1; idx++) {
                Files.move(src.toPath(),dst.toPath().resolve(src.toPath().getFileName()),StandardCopyOption.REPLACE_EXISTING);
                }
                // move src to dst
                Files.move(src.toPath(),dst.toPath().resolve(src.toPath().getFileName()),StandardCopyOption.REPLACE_EXISTING);
                //If StandardCopyOption.REPLACE_EXISTING this option is not used and the file already exists, an exception will be thrown (e.g., FileAlreadyExistsException)
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
