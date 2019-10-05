package main.java;

import java.sql.DriverManager;
import java.sql.SQLException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class MySQLHandler {
	
	String username=""; //Enter in your db username
	String password=""; //Enter your password for the db
	String dbName = "";//Database name
	String dbAddress = "";//Database URL
	String dbPORT = "";//Database port
	String dbSSL = "";//Connection to SQL using SSL true/false
	String url = "jdbc:mysql://"+dbAddress+":"+dbPORT+"/"+dbName+"&useSSL="+dbSSL; //Bringing it all together

	static Connection connection;
	
	public void startSQLConnection() {
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    System.err.println("jdbc driver unavailable!");
		    return;
		}
		try {
			connection = (Connection) DriverManager.getConnection(url,username,password);
			System.out.println("Database connection successful!");
			System.out.println("Setting up the database");
			String table = "CREATE TABLE IF NOT EXISTS Tokens(Something varchar(64));";
			try {
			    PreparedStatement stmt = (PreparedStatement) connection.prepareStatement(table);
			    stmt.executeUpdate();
			} catch (SQLException e) {
			    e.printStackTrace();
			}
		} catch (SQLException e) { //catching errors)
			if(e.getSQLState()=="28000"){
				System.err.println("MySQL Login failed! Check you config.yml");
			}else {
				System.err.println("MySQL Error: "+e.getSQLState());
				e.printStackTrace();
			}
		}
	}
	
	public void stopSQLConnection() {
        try {
            if (connection!=null && !connection.isClosed()){connection.close();}
        } catch(Exception e) {
        	System.out.println("Couldn't close the SQL connection");
        	e.printStackTrace();
        }
	}
}
