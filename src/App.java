import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
public class App extends CLI {

    public static void main(String[] args) throws NoSuchFileException, IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to the Java CLI. Type 'help' for available commands.");

            while (true) {
                System.out.print(currentDirectory.getAbsolutePath() + " $ ");
                String input = scanner.nextLine().trim();
                
                String[] commandParts = input.split(" ");
                String command = commandParts[0];
                if (input.contains(">") || input.contains(">>") || input.contains("|")) {
                  if (command.equals("cat")) {
                     List<String> argsList = Arrays.asList(commandParts).subList(1, commandParts.length);
                     cat.Execute(argsList);
                  } else {
                      handleComplexCommand(input);
                  }
                  continue;
              }
                switch (command) {
                    case "exit" -> {
                        System.out.println("Exiting CLI...");
                        System.exit(0);
                    }

                    case "help" -> printHelp();

                    case "mv" -> mv(commandParts);

                    case "pwd" -> pwd();

                    case "ls" -> {
                        if (commandParts.length > 1) {
                            switch (commandParts[1]) {
                                case "-r":
                                    CLI.listFilesReverse();
                                    break;
                                case "-a":
                                    CLI.listFilesAll();
                                    break;
                                default:
                                    System.out.println("Invalid option for ls. Use -r for reverse or -a for all.");
                            }
                        } else {
                            CLI.listFiles();
                        }
                    }
                    case "cat" -> {
                     // Handle the cat command
                     List<String> argsList = Arrays.asList(commandParts).subList(1, commandParts.length);
                     cat.Execute(argsList);
                  }

                    case "cd" -> {
                        if (commandParts.length >= 1) {
                            changeDirectory(commandParts);
                        } else {
                            System.out.println("Usage: cd <directory>");
                        }
                    }
                    case "rmdir" -> removeDirectory(commandParts[1]);
                    
                    case "mkdir" -> {
                        if (commandParts.length > 1) {
                            makeDirectory(commandParts[1]);
                        } else {
                            System.out.println("Usage: mkdir <directory>");
                        }
                    }
                    case "touch"->{
                    
                        if (commandParts.length > 1) {
                            createFile(commandParts[1]);
                        } else {
                            System.out.println("Usage: touch <filename>");
                        }
                        }
                    case "rm"->{
                        if (commandParts.length > 1) {
                            removeFile(commandParts[1]);
                        }
                        else{
                            System.out.println("Usage: rm <file_or_directory>");
                            return;
                        }
                    }

                    default -> System.out.println("Invalid command. Type 'help' for a list of commands.");
                }
            }
        }
    }

}
