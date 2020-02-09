package net.thirdshift.tokens.database.sqllite;

import net.thirdshift.tokens.Tokens;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLLite extends Database{
    String dbname;
    public SQLLite(Tokens instance){
        super(instance);
        dbname = "tokens_table";
    }


    // SQL creation stuff, You can leave the stuff below untouched.
    // TODO: Actually read and analyze this.
    // This code isn't mine, it was handed out to learn from

    public Connection getSQLConnection() {
        File storageFolder = new File(plugin.getDataFolder(), "Storage");
        if (!storageFolder.exists()){
            storageFolder.mkdirs();
            plugin.getLogger().info("Made /Tokens/Storage/");
        }

        File dataFolder = new File(storageFolder, dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS tokens_table (`player` varchar(32) NOT NULL,`tokens` int(11) NOT NULL,PRIMARY KEY (`player`));";
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("SQLite error: ");
            e.printStackTrace();
        }
    }
}