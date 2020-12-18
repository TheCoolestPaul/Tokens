package net.thirdshift.tokens.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import net.thirdshift.tokens.Tokens;

public class MySQLHandler {

    private final Tokens plugin;
    public String username=""; //Enter in your db username
    public String password=""; //Enter your password for the db
    public String dbName = "";//Database name
    public String dbAddress = "";//Database URL
    public String dbPORT = "";//Database port
    public String dbSSL = "";//Connection to SQL using SSL true/false
    public String url = "";//Putting it all together

    public MySQLHandler(Tokens instance){
        plugin = instance;
    }

    static Connection connection;

    public void updateSettings(){
        this.username = plugin.getConfig().getString("MySQL.Username");
        this.password = plugin.getConfig().getString("MySQL.Password");
        this.dbName = plugin.getConfig().getString("MySQL.Database-Name");
        this.dbPORT = plugin.getConfig().getString("MySQL.Server.Port");
        this.dbAddress = plugin.getConfig().getString("MySQL.Server.Address");
        this.dbSSL = plugin.getConfig().getString("MySQL.Server.SSL");
    }

    public void startSQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) {
            plugin.getLogger().log( Level.SEVERE,
            		String.format("MYSQL ERROR: Unable to find the MySQL Driver. %s", e.getMessage()), e);
            return;
        }
        try {
            url = String.format( "jdbc:mysql://%s:%s/%s?useSSL=%s",
            								dbAddress, dbPORT, dbName, dbSSL );
            connection = DriverManager.getConnection(url,username,password);
            plugin.getLogger().info("Connection to MySQL was successful");
            
            createTable();
        }
        catch (SQLException e) {
            if(e.getSQLState().equals("28000")) {
                plugin.getLogger().warning("MYSQL ERROR: MySQL Login credentials are incorrect. " +
                		"Check you config.yml");
            }
            else if(e.getSQLState().equals("08S01")) {
                plugin.getLogger().severe("MYSQL ERROR: MySQL Couldn't establish a connection!");
            }
            else {
                plugin.getLogger().log( Level.SEVERE,
                		String.format("MYSQL ERROR: Trying to connect to the database. %s", 
                				e.getSQLState()), e);
            }
        }
    }
    
    private void createTable() {
    	 String table ="CREATE TABLE IF NOT EXISTS tokens " +
         		"(uuid VARCHAR(40) NOT NULL, num INT(9) NOT NULL, UNIQUE (uuid));";
         
         try ( PreparedStatement stmt = connection.prepareStatement(table); ) {
         	stmt.executeUpdate();
         } 
         catch (SQLException e) {
         	plugin.getLogger().log( Level.SEVERE,
         			String.format("MYSQL ERROR: Trying to create tokens table failed. %s", 
         					e.getSQLState()), e);
         }
    }

    public void stopSQLConnection() {
        try {
            if (connection!=null && !connection.isClosed()){
                connection.close();
            }
        } 
        catch(Exception e) {
            plugin.getLogger().log( Level.SEVERE,
            		String.format("MYSQL ERROR: Failure trying to close the connection. %s", 
            				e.getMessage()), e);
        }
    }

//    public void addTokens(Player player, int tokensIn){
//        this.setTokens(player, (this.getTokens(player)+tokensIn) );
//    }

    public void removeTokens(Player player, int tokensIn) {
    	// Can't have less than 0 tokens
        this.setTokens(player, Math.max( (this.getTokens(player)-tokensIn),0 ));
    }

    public int getTokens(Player player){
        int tokens = 0;
        String query = "SELECT num FROM tokens WHERE uuid = ?";
        try ( PreparedStatement ps = connection.prepareStatement(query); ) {
            ps.setString(1, player.getUniqueId().toString());
            
            try ( ResultSet result = ps.executeQuery(); ) {
            	
            	if (result == null){
            		setTokens(player, 0);
            	}
            	else {
            		while (result.next()) {
            			tokens = result.getInt("num");
            			break;
            		}
            	}
            }
        }
        catch(SQLException e) {
            plugin.getLogger().log( Level.WARNING,
            		String.format("MYSQL ERROR: getTokens: %s", e.getMessage()), e);
        }
        return tokens;
    }

    /**
     * <p>If the addTokens function is unable to add tokens to the specified
     * player, due to lack of being within the database, then this function will
     * add them.  This uses an insert statement to add a new record for the player
     * with the fallback being that if the player already exists (because uuid 
     * has a unique constraint) then it will update the record instead.
     * If this function is only called fro addToken, then it will never use
     * the update aspect of this sql statement.
     * </p>
     * 
     * @param player
     * @param tokens
     * @return
     */
    public int setTokens(Player player, int tokens) {
    	int results = 0;
        String query = "INSERT INTO tokens (uuid, num) VALUES (?, ?) " +
        		"ON DUPLICATE KEY UPDATE num = VALUES(num);";
        try ( PreparedStatement ps = connection.prepareStatement(query); ) {
        	ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, tokens);
            results = ps.executeUpdate();
        } 
        catch(SQLException e) {
            plugin.getLogger().log( Level.WARNING,
            		String.format("MYSQL ERROR: setTokens: %s", e.getMessage()), e);
        }
        return results;
    }
    
    /**
     * <p>This function will add the given quantity of tokens to the the player's
     * current amount. This will be performed with just one transaction. Code that 
     * will be calling this function will always have the assurance that the player
     * will already exist, so there is no need to pre-read to get the value, or to
     * check if the record is there.  That said, the only condition where the 
     * record will not be there will be for the first transaction for that player.
     * To deal with that condition, the results of this function will return a 
     * value of 0 to indicate that no records were updated, therefore that will
     * prompt us to be able to insert it.
     * </p>
     * 
     * <p>Use of REPLACE is an expensive operation that first
     * performs a delete and then an insert (two transactions), while
     * an UPDATE only performs one transaction.  The results of executeUpdate 
     * indicate how rows have been updated, since there is only one row per player
     * we find that UPDATE return a value of 1, and REPLACE will return a value of 2
     * because it first deletes the entry then adds it back.  If the results is
     * zero, then that indicates there isn't a record for the players, so then add 
     * one with calling setTokens.
     * </p>
     * 
     * @param player
     * @param tokens
     * @return
     */
    public int addTokens(Player player, int tokens) {
    	int results = 0;
    	String query = "UPDATE tokens set num = num + ? where uuid = ?";
    	try ( PreparedStatement ps = connection.prepareStatement(query); ) {
    		ps.setInt(1, tokens);
    		ps.setString(2, player.getUniqueId().toString());
    		
    		results = ps.executeUpdate();
    		if ( results == 0 ) {
    			results = setTokens( player, tokens );
    		}
    	} 
    	catch(SQLException e) {
            plugin.getLogger().log( Level.WARNING,
            		String.format("MYSQL ERROR: addTokens: %s", e.getMessage()), e);
    	}
    	return results;
    }
}

