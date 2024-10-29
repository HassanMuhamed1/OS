import java.io.File;
public class CLI {
    protected  static File currentDirectory = new File(System.getProperty("user.dir"));

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
        // cd /
        if(command.length==2 && command[1].equals("/")){
            String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
            String rootDir = userDir.substring(0, userDir.indexOf(File.separator)+1);
            currentDirectory = new File(rootDir);
        }
        // cd ..
        if(command.length==2 && command[1].equals("..") ){
            String currentPath = currentDirectory.getParent();
            
            if (currentPath !=null){
                currentDirectory = new File(currentPath);                
            }
            else {
                System.out.println("You are in root directory can't back!");
            }
            return;
        }
        // cd [directory_name]
        if(command.length==2 && command[1].matches("^[a-zA-Z0-9-_ ]+$")){
            File newDir = new File(currentDirectory, command[1]);
            if (newDir.exists() && newDir.isDirectory()) {
                currentDirectory = newDir;
            } else {
                System.out.println("Invalid directory: " + command[1]);
            }
            // return;
        }
       // cd ~ 
        if(command.length==2 && command[1].equals("~")){
            currentDirectory = new File(System.getProperty("user.home"));
            String homeDir = currentDirectory.getAbsolutePath();
            currentDirectory = new File(homeDir);
        }
        // cd dir_1/dir_2/dir_3
        if(command.length==2 && command[1].matches("^[a-zA-Z0-9-_ ]+$")){
            File newDir = new File(currentDirectory, command[1]);
            if (newDir.exists() && newDir.isDirectory()) {
                currentDirectory = newDir;
            } else {
                System.out.println("Invalid directory: " + command[1]);
            }
            // return;
        }
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

    protected static void pwd(){
        System.out.println(currentDirectory);
    }

}
