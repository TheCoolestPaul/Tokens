package net.thirdshift.tokens.database.sqllite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import net.thirdshift.tokens.Tokens;


public abstract class Database {
    private Tokens plugin;
    private Connection connection;

    private final String table = "tokens_table";
    public Database(Tokens instance) {
        plugin = instance;
    }

    protected Tokens getPlugin() {
    	return plugin;
    }
    
    /**
     * <p>This establishes the SQLite connection if it does not exist,
     * and then upon subsequent calls, returns that connection. 
     * A simple check with low overhead (if connection is null or closed) 
     * is performed to establish if the more expensive checks and operations
     * are required (file checks and establishing database connections).
     * </p>
     * 
     */
    public Connection getSQLConnection() {
    	try {
			if ( connection == null || connection.isClosed() ) {
				// Once the connection is established, then skip all of this:
				openConnection();
			}
		}
		catch ( SQLException e ) {
			plugin.getLogger().log( Level.SEVERE,
            		String.format("Database ERROR: cannot establish if the connection is closed: %s", 
            				e.getMessage()), e);
		}

    	return connection;
    };
    
    protected void setConnection( Connection connection ) {
    	this.connection = connection;
    }

    protected abstract void openConnection();
    
    public abstract void load();

    public Integer getTokens(Player player) {
    	Integer results = 0;
    	
    	String sql = String.format( "SELECT * FROM %s WHERE player = ?;", table );
        try (   Connection conn = getSQLConnection();
        		PreparedStatement ps = conn.prepareStatement( sql );
        		) {
            ps.setString(1, player.getUniqueId().toString());
            
            try ( ResultSet rs = ps.executeQuery(); ) {
            	
            	while(rs.next()){
            		// If the query was selected by player's uuid, not sure why it has to be retested here:
            		if(rs.getString("player").equalsIgnoreCase(player.getUniqueId().toString())){
            			results = rs.getInt("tokens");
            		}
            	}
            }
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        }
        return results;
    }

    public void setTokens(Player player, Integer tokens) {
    	String sql = String.format( "REPLACE INTO %s (player,tokens) VALUES(?,?)", table );
        try (   Connection conn = getSQLConnection();
        		PreparedStatement ps = conn.prepareStatement( sql );
        		) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, tokens);
            
            ps.executeUpdate();
        } 
        catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        }
    }
    
    /**
     * This function will add the given number of tokens to the player's balance without
     * having to first perform a read from the database.  This will reduce overhead and
     * improve performance.
     * 
     * This function uses try with resources to ensure all resources are properly closed.
     * 
     * @param player
     * @param tokens
     */
    public int addTokens(Player player, Integer tokens) {
    	int results = 0;
    	String sql = String.format( "UPDATE %s set tokens = tokens + ? " +
							" where player = ? ", table );
    	try (   Connection conn = getSQLConnection();
    			PreparedStatement ps = conn.prepareStatement( sql );
    		) {
    		ps.setInt(1, tokens);
    		ps.setString(2, player.getUniqueId().toString());
    		
    		results = ps.executeUpdate();
    	} 
    	catch (SQLException ex) {
    		plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
    	}
    	return results;
    }
}