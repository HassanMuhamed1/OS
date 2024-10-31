import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
public class cat {
    
        public static void Display(String fileName) {
            File file = new File(CLI.currentDirectory, fileName);
            if (!file.exists()) {
                System.out.println("File not found: " + fileName);
                return;
            }
        
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
        
        //============================================================================================================
        // function to write content to a file or overwrite (>)
        public static void Write(String fileName, String content)
        {
            File file = new File(CLI.currentDirectory, fileName);
            try (FileWriter writer = new FileWriter(file))
            {
                writer.write(content);
            } 
            catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        }
    //============================================================================================================
        // function to append content to a file (>>)
        public static void Append(String fileName, String content) {
            File file = new File(CLI.currentDirectory, fileName); 
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(content);
            } catch (IOException e) {
                System.out.println("Error appending to file: " + e.getMessage());
            }
        }
    //============================================================================================================
        // function to handle multiple files or redirections
        public static void Execute(List<String> args) {
            if (args.size() == 1) {
                Display(args.get(0));
            } else if (args.size() > 2) {
                String option = args.get(args.size() - 2);
                String targetFile = args.get(args.size() - 1);
                StringBuilder content = new StringBuilder();
        
                for (int i = 0; i < args.size() - 2; i++) {
                    File file = new File(CLI.currentDirectory, args.get(i));
                    if (!file.exists()) {
                        System.out.println("File not found: " + args.get(i));
                        return;
                    }
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append(System.lineSeparator());
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading file: " + args.get(i));
                    }
                }
        
                switch (option) {
                    case ">" -> Write(targetFile, content.toString());
                    case ">>" -> Append(targetFile, content.toString());
                    default -> System.out.println("Invalid option: " + option);
                }
            } else if (args.size() > 1) {
                for (String fileName : args) {
                    File file = new File(CLI.currentDirectory, fileName);
                    if (!file.exists()) {
                        System.out.println("File not found: " + fileName);
                        continue;
                    }
                    System.out.println("Displaying contents of file: " + fileName);
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading file: " + fileName);
                    }
                }
            } else {
                System.out.println("Usage: cat <file1> [<file2> ...] [> or >>] <targetFile>");
            }
        }
}
