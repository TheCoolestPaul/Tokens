package net.thirdshift.database.sqllite;

import net.thirdshift.Tokens;

import java.util.logging.Level;

public class Error {
    public static void execute(Tokens plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(Tokens plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}