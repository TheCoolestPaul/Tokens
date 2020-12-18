package net.thirdshift.tokens.database.sqllite;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import net.thirdshift.tokens.Tokens;

public class SQLLite extends Database{
    private String dbname;
    
    public SQLLite(Tokens instance){
        super(instance);
        dbname = "tokens_table";
    }


    // SQL creation stuff, You can leave the stuff below untouched.
    // TODO: Actually read and analyze this.
    // This code isn't mine, it was handed out to learn from

//    /**
//     * <p>This establishes the SQLite connection if it does not exist,
//     * and then upon subsequent calls, returns that connection. 
//     * A simple check with low overhead (if connection is null or closed) 
//     * is performed to establish if the more expensive checks and operations
//     * are required (file checks and establishing database connections).
//     * </p>
//     * 
//     */
//    public Connection getSQLConnection() {
//    	try {
//			if ( connection == null || connection.isClosed() ) {
//				// Once the connection is established, then skip all of this:
//				openConnection();
//			}
//		}
//		catch ( SQLException e ) {
//			plugin.getLogger().log( Level.SEVERE,
//            		String.format("SQLite ERROR: cannot establish if the connection is closed: %s", 
//            				e.getMessage()), e);
//		}
//
//    	return connection;
//    }

    @Override
    protected void openConnection() {
        File storageFolder = new File(getPlugin().getDataFolder(), "Storage");
        if (!storageFolder.exists()){
            if(storageFolder.mkdirs())
            	getPlugin().getLogger().info("SQLite openConnection: Created dirs: plugins/Tokens/Storage/");
        }

        String databaseFileName = dbname + ".db";
        File dataFolder = new File(storageFolder, databaseFileName);
        if (!dataFolder.exists()){
            try {
                if(dataFolder.createNewFile())
                	getPlugin().getLogger().info( String.format( 
                    		"SQLite openConnection: Created file: /Tokens/%s", databaseFileName));
            } 
            catch (IOException e) {
            	getPlugin().getLogger().log(Level.SEVERE, String.format( 
                		"SQLite openConnection: Could not created file: /Tokens/%s", databaseFileName));
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            setConnection( DriverManager.getConnection("jdbc:sqlite:" + dataFolder) );
        } 
        catch (SQLException ex) {
        	getPlugin().getLogger().log(Level.SEVERE,
            			"SQLite openConnection: Failure establishing a connection to the database", ex);
        } 
        catch (ClassNotFoundException ex) {
        	getPlugin().getLogger().log(Level.SEVERE, "" +
            		"SQLite openConnection: The SQLite JDBC Driver was not found. " +
            		"You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
    }
    
    public void load() {
        try ( Statement s = getSQLConnection().createStatement(); ) {
            String sql = 
            		"CREATE TABLE IF NOT EXISTS tokens_table " +
            		"(`player` varchar(32) NOT NULL,`tokens` int(11) NOT NULL, PRIMARY KEY (`player`));";
            s.executeUpdate(sql);
        } 
        catch (SQLException e) {
            getPlugin().getLogger().log( Level.SEVERE,
            		String.format("SQLite ERROR: cannot create tokens_table: %s", e.getMessage()), e);
        }
    }
}