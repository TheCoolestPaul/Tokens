package net.thirdshift.database.mysql;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import net.thirdshift.Tokens;
import org.bukkit.entity.Player;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLHandler {

	private Tokens plugin;
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
	
	public void startSQLConnection() {
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    System.err.println("jdbc driver unavailable!");
			plugin.mysqlEnabled = false;
		    return;
		}
		try {
			url = "jdbc:mysql://"+dbAddress+":"+dbPORT+"/"+dbName+"?useSSL="+dbSSL;
			connection = (Connection) DriverManager.getConnection(url,username,password);
			plugin.getLogger().info("Connection to MySQL was successful");

			String table ="CREATE TABLE IF NOT EXISTS tokens ( uuid VARCHAR(40) NOT NULL , num INT(9) NOT NULL , UNIQUE (uuid));";
			try {
			    PreparedStatement stmt = (PreparedStatement) connection.prepareStatement(table);
			    stmt.executeUpdate();
			} catch (SQLException e) {
			    e.printStackTrace();
			}
		} catch (SQLException e) { //catching errors)
			if(e.getSQLState()=="28000") {
				plugin.getLogger().warning("MySQL Login information was wrong! Check you config.yml");
				plugin.mysqlEnabled = false;
			}else if(e.getSQLState()=="08S01"){
				plugin.getLogger().warning("MySQL Couldn't establish a connection!");
				plugin.mysqlEnabled = false;
			}else {
				System.err.println("MySQL Error: "+e.getSQLState());
				e.printStackTrace();
				plugin.mysqlEnabled = false;
			}
		}
	}
	
	public void stopSQLConnection() {
		// invoke on disable.
		try { //using a try catch to catch connection errors (like wrong sql password...)
			if (connection!=null && !connection.isClosed()){ //checking if connection isn't null to
				//avoid receiving a nullpointer
				connection.close(); //closing the connection field variable.
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public int getTokens(Player player){
		int tokens = 0;
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT num FROM tokens WHERE uuid = '"+player.getUniqueId().toString()+"';");
			if (result == null){
				setTokens(player, 0);
			}else {
				while (result.next()) {
					tokens = result.getInt("num");
				}
			}
		} catch(SQLException e) {
			plugin.getLogger().info("MYSQL ERROR");
			e.printStackTrace();
		}
		return tokens;
	}

	public void setTokens(Player player, int tokens){
		try {
			Statement statement = connection.createStatement();
			String state = "UPDATE tokens SET num = "+tokens+" WHERE uuid = '"+player.getUniqueId().toString()+"';";
			int changed = statement.executeUpdate(state);
			if (changed==0){
				String state2 = "INSERT INTO tokens (uuid, num) VALUES ('"+player.getUniqueId().toString()+"', "+tokens+");";
				statement.execute(state2);
			}else{
				plugin.getLogger().info("Updated a player in the database.");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
