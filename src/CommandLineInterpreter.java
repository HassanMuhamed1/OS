import java.io.File;
import java.util.Scanner;
public class CommandLineInterpreter {

    private static File currentDirectory = new File(System.getProperty("user.dir"));

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

                    case "pwd" -> System.out.println(currentDirectory.getAbsolutePath());

                    case "ls" -> listFiles();

                    case "cd" -> {
                        if (commandParts.length > 1) {
                            changeDirectory(commandParts[1]);
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

    private static void printHelp() {
        System.out.println("Available Commands:");
        System.out.println("pwd          - Print the current directory");
        System.out.println("ls           - List files in the current directory");
        System.out.println("cd <dir>     - Change directory");
        System.out.println("mkdir <dir>  - Create a new directory");
        System.out.println("exit         - Exit the CLI");
        System.out.println("help         - Show available commands");
    }

    private static void listFiles() {
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("No files found.");
        }
    }

    private static void changeDirectory(String dirName) {
        File newDir = new File(currentDirectory, dirName);
        if (newDir.exists() && newDir.isDirectory()) {
            currentDirectory = newDir;
        } else {
            System.out.println("Invalid directory: " + dirName);
        }
    }

    private static void makeDirectory(String dirName) {
        File newDir = new File(currentDirectory, dirName);
        if (newDir.exists()) {
            System.out.println("Directory already exists: " + dirName);
        } else if (newDir.mkdir()) {
            System.out.println("Directory created: " + dirName);
        } else {
            System.out.println("Failed to create directory: " + dirName);
        }
    }
}
