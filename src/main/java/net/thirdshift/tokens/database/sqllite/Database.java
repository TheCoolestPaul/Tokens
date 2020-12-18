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
				plugin.getLogger().log( Level.INFO,
						String.format("Database Connection: opening a connection to the " +
											"SQLite database." ));
				boolean success = openConnection();
				if ( !success ) {
					
					plugin.getLogger().log( Level.SEVERE,
							String.format("Database ERROR: was unable to open the SQLite database " +
											"connection. No additional information is available." ));
				}
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

    protected abstract boolean openConnection();
    
    public abstract void load();
    
    public void closeConnection() {
    	try {
			if ( connection != null && !connection.isClosed() ) {
				
				connection.close();
				
				plugin.getLogger().log( Level.INFO,
						String.format("Database Connection:closed the connection to the " +
											"SQLite database." ));
			}
		}
		catch ( SQLException e ) {
			plugin.getLogger().log( Level.SEVERE,
            		String.format("Database ERROR: cannot close the MYSql connection: %s", 
            				e.getMessage()), e);
		}
    }

    public Integer getTokens(Player player) {
    	Integer results = 0;
    	
    	String sql = String.format( "SELECT * FROM %s WHERE player = ?;", table );
    	
    	// Never let the try-with-resources close the connection:
    	Connection conn = getSQLConnection();
    	
    	boolean hasEntry = false;
        try (   PreparedStatement ps = conn.prepareStatement( sql );
        		) {
            ps.setString(1, player.getUniqueId().toString());
            
            try ( ResultSet rs = ps.executeQuery(); ) {
            	
            	while(rs.next()){
            		// If the query was selected by player's uuid, not sure why it has to be retested here:
            		if(rs.getString("player").equalsIgnoreCase(player.getUniqueId().toString())){
            			results = rs.getInt("tokens");
            			hasEntry = true;
            		}
            	}
            }
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        }
        if ( !hasEntry ) {
        	setTokens( player, 0 );
        }
        return results;
    }

    public void setTokens(Player player, Integer tokens) {
    	String sql = String.format( "REPLACE INTO %s (player,tokens) VALUES(?,?)", table );
    	
    	// Never let the try-with-resources close the connection:
    	Connection conn = getSQLConnection();

        try (   PreparedStatement ps = conn.prepareStatement( sql );
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
    	
    	// Never let the try-with-resources close the connection:
    	Connection conn = getSQLConnection();

    	try (   PreparedStatement ps = conn.prepareStatement( sql );
    		) {
    		ps.setInt(1, tokens);
    		ps.setString(2, player.getUniqueId().toString());
    		
    		results = ps.executeUpdate();
    	} 
    	catch (SQLException ex) {
    		plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
    	}
    	
    	if ( results == 0 ) {
    		setTokens( player, tokens );
    	}
    	return results;
    }
}