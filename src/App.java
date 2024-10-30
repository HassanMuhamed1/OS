
import java.util.Scanner;
public class App extends CLI {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to the Java CLI. Type 'help' for available commands.");

            while (true) {
                System.out.print(currentDirectory.getAbsolutePath() + " $ ");
                String input = scanner.nextLine().trim();
                String[] commandParts = input.split(" ");
                String command = commandParts[0];

                switch (command) {
                    case "exit" -> {
                        System.out.println("Exiting CLI...");
                        System.exit(0);
                    }

                    case "help" -> printHelp();

                    case "mv" -> mv();

                    case "pwd" -> pwd();

                    case "ls" -> listFiles();

                    case "cd" -> {
                        if (commandParts.length >= 1) {
                            changeDirectory(commandParts);
                        } else {
                            System.out.println("Usage: cd <directory>");
                        }
                    }

                    case "mkdir" -> {
                        if (commandParts.length > 1) {
                            makeDirectory(commandParts[1]);
                        } else {
                            System.out.println("Usage: mkdir <directory>");
                        }
                    }

                    default -> System.out.println("Invalid command. Type 'help' for a list of commands.");
                }
            }
        }
    }

}
