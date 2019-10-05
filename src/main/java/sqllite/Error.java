package main.java.sqllite;

import java.util.logging.Level;
import main.java.Tokens; // import your main class

public class Error {
    public static void execute(Tokens plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(Tokens plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}