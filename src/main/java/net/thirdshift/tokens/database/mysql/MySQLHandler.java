package net.thirdshift.tokens.database.mysql;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.database.sqllite.Errors;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.logging.Level;

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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
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
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            if(e.getSQLState().equals("28000")) {
                plugin.getLogger().warning("MySQL Login information was wrong! Check you config.yml");
            }else if(e.getSQLState().equals("08S01")){
                plugin.getLogger().severe("MySQL Couldn't establish a connection!");
            }else {
                System.err.println("MySQL Error: "+e.getSQLState());
                e.printStackTrace();
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
        PreparedStatement ps = null;
        try {
            String query = "SELECT num FROM tokens WHERE uuid = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, player.getUniqueId().toString());
            ResultSet result = ps.executeQuery();
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
        } finally{
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return tokens;
    }

    public void setTokens(Player player, int tokens){
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        try {
            String query = "UPDATE tokens SET num = ? WHERE uuid = ?;";
            ps1 = connection.prepareStatement(query);
            ps1.setInt(1, tokens);
            ps1.setString(2, player.getUniqueId().toString());
            int changed = ps1.executeUpdate();
            if (changed==0){
                String query2 = "INSERT INTO tokens (uuid, num) VALUES (?, ?);";
                ps2 = connection.prepareStatement(query2);
                ps2.setString(1, player.getUniqueId().toString());
                ps2.setInt(2, tokens);
                ps2.executeUpdate();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (ps1 != null)
                    ps1.close();
                if (ps2 != null)
                    ps2.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
}

