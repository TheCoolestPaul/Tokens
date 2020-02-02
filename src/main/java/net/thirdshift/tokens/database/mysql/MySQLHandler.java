package net.thirdshift.tokens.database.mysql;

import net.thirdshift.tokens.Tokens;
import org.bukkit.entity.Player;

import java.sql.*;

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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            plugin.mysqlEnabled = false;
            return;
        }
        try {
            url = "jdbc:mysql://"+dbAddress+":"+dbPORT+"/"+dbName+"?useSSL="+dbSSL;
            connection = DriverManager.getConnection(url,username,password);
            plugin.getLogger().info("Connection to MySQL was successful");

            String table ="CREATE TABLE IF NOT EXISTS tokens ( uuid VARCHAR(40) NOT NULL , num INT(9) NOT NULL , UNIQUE (uuid));";
            try {
                PreparedStatement stmt = connection.prepareStatement(table);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) { //catching errors)
            if(e.getSQLState().equals("28000")) {
                plugin.getLogger().warning("MySQL Login information was wrong! Check you config.yml");
                plugin.mysqlEnabled = false;
            }else if(e.getSQLState().equals("08S01")){
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
        try {
            if (connection!=null && !connection.isClosed()){
                connection.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addTokens(Player player, int tokensIn){
        this.setTokens(player, (this.getTokens(player)+tokensIn) );
    }

    public void removeTokens(Player player, int tokensIn){
        this.setTokens(player, Math.max( (this.getTokens(player)-tokensIn),0 ));// Can't have less than 0 tokens
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

