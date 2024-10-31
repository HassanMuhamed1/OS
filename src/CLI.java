import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CLI {
   protected static File currentDirectory = new File(System.getProperty("user.dir"));

   private static File getRoot() {
      File currentRoot;
      for(currentRoot = new File(System.getProperty("user.dir")); currentRoot.getParent() != null; currentRoot = new File(currentRoot.getParent())) {
      }
      return currentRoot;
   }

   public static void printHelp() {
      System.out.println("Available Commands:");
      System.out.println("pwd          - Print the current directory");
      System.out.println("ls           - List files in the current directory");
      System.out.println("ls -r        - List files in the current directory in reverse order");
      System.out.println("ls -a        - List files in the current directory include hidden files");
      System.out.println("cd <dir>     - Change directory");
      System.out.println("mkdir <dir>  - Create a new directory");
      System.out.println("touch <file> - Create a new file");
      System.out.println("rm <file>    - Remove a file.");
      System.out.println(">> <file>    - Append output to the specified file (similar to '>>' in Linux, which appends data to a file without overwriting).");
      System.out.println(">  <file>    - Overwrite the specified file (similar to '>' in Linux, which overwrites the file or creates a new one if it doesn't exist).");
      System.out.println("exit         - Exit the CLI");
      System.out.println("help         - Show available commands");
   }

   protected static void listFiles() {
      File[] files = currentDirectory.listFiles();
      if (files != null) {
         File[] var1 = files;
         int var2 = files.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            File file = var1[var3];
            if (!file.isHidden()) {
               System.out.println(file.getName());
            }
         }
      } else {
         System.out.println("No files found.");
      }

   }

   public static void listFilesAll() {
      File[] files = currentDirectory.listFiles();
      if (files != null) {
         File[] var1 = files;
         int var2 = files.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            File file = var1[var3];
            System.out.println(file.getName());
         }
      } else {
         System.out.println("No files found.");
      }

   }

   protected static void listFilesReverse() {
      File[] files = currentDirectory.listFiles();
      if (files != null) {
         Arrays.sort(files, Collections.reverseOrder());
         File[] var1 = files;
         int var2 = files.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            File file = var1[var3];
            if(!file.isHidden()){
                System.out.println(file.getName());
            }
         }
      } else {
         System.out.println("No files found.");
      }

   }

   protected static void listFilesToFile(String filename, boolean append) {
      try {
         FileWriter writer = new FileWriter(new File(currentDirectory, filename), append);

         try {
            File[] files = currentDirectory.listFiles();
            if (files == null) {
               System.out.println("No files found.");
            } else {
               File[] var4 = files;
               int var5 = files.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  File file = var4[var6];
                  String var10001 = file.getName();
                  writer.write(var10001 + System.lineSeparator());
               }

               System.out.println("Output written to " + filename);
            }
         } catch (Throwable var9) {
            try {
               writer.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         writer.close();
      } catch (IOException var10) {
         System.out.println("Error writing to file: " + var10.getMessage());
      }

   }

   protected static void changeDirectory(String[] command) {
      if (command.length == 1) {
         currentDirectory = new File(System.getProperty("user.home"));
      } else if (command.length == 2 && command[1].equals("/")) {
         File rootDirectory = getRoot();
         currentDirectory = rootDirectory;
      } else if (command.length == 2 && command[1].equals("~")) {
         currentDirectory = new File(System.getProperty("user.home"));
      } else {
         String targetPath;
         if (command.length == 2 && command[1].equals("..")) {
            targetPath = currentDirectory.getParent();
            if (targetPath != null) {
               currentDirectory = new File(targetPath);
            } else {
               System.out.println("You are in the root directory, can't go back further!");
            }

         } else {
            if (command.length == 2) {
               targetPath = command[1].trim();
               File newDir;
               if (isAbsolutePath(targetPath)) {
                  newDir = new File(targetPath);
               } else {
                  newDir = resolveRelativePath(targetPath);
               }

               if (newDir != null && newDir.exists() && newDir.isDirectory()) {
                  currentDirectory = newDir.getAbsoluteFile();
               } else {
                  System.out.println("Invalid directory: " + targetPath);
               }
            }

         }
      }
   }

   private static boolean isAbsolutePath(String path) {
      return path.startsWith(File.separator) || path.matches("^[A-Z]:.*");
   }

   private static File resolveRelativePath(String path) {
      String[] parts = path.split("[/\\\\]");
      File dir = currentDirectory;
      String[] var3 = parts;
      int var4 = parts.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String part = var3[var5];
         if (!part.isEmpty()) {
            File[] matchingDirs = dir.listFiles(File::isDirectory);
            if (matchingDirs == null) {
               System.out.println("Error: Unable to access " + dir.getAbsolutePath());
               return null;
            }

            List<File> matches = Arrays.stream(matchingDirs).filter((f) -> {
               return f.getName().equals(part);
            }).toList();
            if (matches.isEmpty()) {
               System.out.println("Invalid directory: " + part);
               return null;
            }

            dir = (File)matches.get(0);
         }
      }

      return dir;
   }

   protected static String pwd() {
      System.out.println("Current Directory: " + currentDirectory);
      return currentDirectory.getAbsolutePath();
  }
   protected static void pwdToFile(String filename, boolean append) {
      try (FileWriter writer = new FileWriter(new File(currentDirectory, filename), append)) {
          writer.write(pwd() + System.lineSeparator());
          System.out.println("Output written to " + filename);
      } catch (IOException e) {
          System.out.println("Error writing to file: " + e.getMessage());
      }
  }

   protected static void pipe(String[] commands) {
      if (commands.length < 2) {
          throw new IllegalArgumentException("Usage: command_1 | command_2 | command_3 | .... | command_N ");
      }
      
      if (commands[0].equals("ls") && commands[1].equals("sort")) {
          File[] files = currentDirectory.listFiles();
          if (files != null) {
              Arrays.stream(files)
                    .map(File::getName)
                    .sorted()
                    .forEach(System.out::println);
          } else {
              System.out.println("No files found!");
          }
      } else {
          System.out.println("Unsupported command: " + String.join(" ", commands));
      }
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
  
   public static File makeAbsolute(String sourcePath) throws IOException, NoSuchFileException {
      File f = new File(sourcePath);
      if (!f.isAbsolute()) {
         f = new File(currentDirectory.getAbsolutePath(), sourcePath);
      }

      return f.getAbsoluteFile();
   }


   protected static void makeDirectory(String dirName) {
      File newDir = new File(currentDirectory, dirName);
      if (newDir.exists()) {
         System.out.println("Directory already exists: " + dirName);
      } else if (!newDir.mkdir()) {
         if (newDir.mkdir()) {
            System.out.println("Directory created: " + dirName);
         } else {
            System.out.println("Failed to create directory: " + dirName);
         }
      }

   }

   protected static void removeDirectory(String dirName) {
      File newDir = new File(currentDirectory, dirName);
      if (!newDir.exists()) {
         System.out.println("Directory Does not exists: " + dirName);
      } else if (newDir.delete()) {
         System.out.println("Directory Removed.");
      } else {
         System.out.println("Failed to remove " + dirName + ":Directory not empty ");
      }

   }

   protected static void createFile(String fileName) {
      File file = new File(currentDirectory, fileName);
      if (file.exists()) {
         System.out.println("File already exists: " + fileName);
      } else {
         try {
            if (file.createNewFile()) {
               System.out.println("File created: " + fileName + " in directory " + currentDirectory.getAbsolutePath());
            } else {
               System.out.println("Failed to create file: " + fileName);
            }
         } catch (IOException var3) {
            System.out.println("An error occurred while creating the file.");
            var3.printStackTrace();
         }
      }

   }

   protected static void removeFile(String fileName) {
      File file = new File(currentDirectory, fileName);
      if (!file.exists()) {
         System.out.println("File does not exist: " + fileName);
      } else {
         try {
            if (Files.deleteIfExists(file.toPath())) {
               System.out.println("File deleted successfully: " + fileName);
            } else {
               System.out.println("Failed to delete file: " + fileName);
            }
         } catch (IOException var3) {
            System.out.println("An error occurred while trying to delete the file: " + fileName);
            var3.printStackTrace();
         }
      }

   }

   protected static void handleComplexCommand(String input) throws IOException {
      String[] parts;
      if (input.contains("|")) {
         parts = input.split("\\|");

         for(int i = 0; i < parts.length; ++i) {
            parts[i] = parts[i].trim();
         }

         pipe(parts);
      } else if (input.contains(">") || input.contains(">>")) {
         parts = input.split(" ");
         String command = parts[0];
         String filename = parts[parts.length - 1];
         if (parts[parts.length - 2].equals(">")) {
            if (command.equals("ls")) {
               listFilesToFile(filename, false);
            } else if (command.equals("pwd")) {
               pwdToFile(filename, false);
            }
         } else if (parts[parts.length - 2].equals(">>")) {
            if (command.equals("ls")) {
               listFilesToFile(filename, true);
            } else if (command.equals("pwd")) {
               pwdToFile(filename, true);
            }
         }
      }

   }

 private static String readFileContent(File file) {
   StringBuilder content = new StringBuilder();
   try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
       String line;
       while ((line = reader.readLine()) != null) {
           content.append(line).append("\n");
       }
   } catch (IOException e) {
       System.out.println("Error reading file: " + file.getName());
   }
   return content.toString();
}

public static void cat(String[] command) {
   if (command.length < 2) {
       System.out.println("Usage: cat <file1> [<file2> ...] [> or >>] <targetFile>");
       return;
   }

   boolean overwrite = false;
   boolean append = false;
   String targetFileName = null;

   // Check for redirection
   if (command[command.length - 2].equals(">")) {
       overwrite = true;
       targetFileName = command[command.length - 1];
   } else if (command[command.length - 2].equals(">>")) {
       append = true;
       targetFileName = command[command.length - 1];
   }

   // Determine the number of input files
   int fileCount = (targetFileName != null) ? command.length - 2 : command.length - 1;

   StringBuilder combinedContent = new StringBuilder();

   for (int i = 1; i <= fileCount; i++) {
       File inputFile = new File(command[i]); // Assuming the path is correct
       combinedContent.append(readFileContent(inputFile));
   }

   // Output to console or write to target file
   if (targetFileName != null) {
       File targetFile = new File(targetFileName);
       try (FileWriter writer = new FileWriter(targetFile, append)) {
           writer.write(combinedContent.toString());
       } catch (IOException e) {
           System.out.println("Error writing to file: " + e.getMessage());
       }
   } else {
       System.out.print(combinedContent.toString());
   }
}
}

