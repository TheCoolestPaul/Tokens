package main.java;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import main.java.commands.CMDTokens;
import main.java.sqllite.Database;
import main.java.sqllite.SQLite;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Tokens extends JavaPlugin {
	String storageType = "sqllite";
	private MySQLHandler mysql;
	private MongoDB mongo;
	private Database sqllite;
	
	@Override
    public void onEnable() {
		this.saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new TokenListeners(), this);
		storageType = this.getConfig().getString("Storage-type");
		System.out.println("Storage type is "+storageType);
		if( storageType=="mysql" ) {
			MySQLHandler mysql = new MySQLHandler(); 
			System.out.println("Connecting to the database");
			mysql.username = this.getConfig().getString("MySQL.Username");
			mysql.password = this.getConfig().getString("MySQL.Password");
			mysql.dbName = this.getConfig().getString("MySQL.Database-Name");
			mysql.dbPORT = this.getConfig().getString("MySQL.Server.Port");
			mysql.dbAddress = this.getConfig().getString("MySQL.Server.Address");
			mysql.dbSSL = this.getConfig().getString("MySQL.Server.SSL");
			mysql.startSQLConnection();
		}else if( storageType=="mongodb" ){
			mongo = new MongoDB();
			mongo.connect("localhost", 27017);
		}else {
			this.sqllite = new SQLite(this);
	        this.sqllite.load();
		}
		
    	System.out.println("Enabling commands");
    	this.getCommand("tokens").setExecutor(new CMDTokens(this));
    	
    }
	
    @Override
    public void onDisable() {
    	if( storageType=="mysql" ) {
	    	System.out.println("Disconnecting from the database ");
	    	mysql.stopSQLConnection();
    	}else if( storageType=="mongodb" ){
    		for(Player player : Bukkit.getOnlinePlayers()) {
    			System.out.println("Saving player "+player.getName());
    			mongo.pluginDisableSavePlayers(player, mongo.getPlayerTokens(player));
    		}
    	}
    	//Don't have to do anything for SQLLite <3
    }
    
    public Database getRDatabase() {
        return this.sqllite;
    }
    
    public class TokenListeners implements Listener{
	    @EventHandler
	    public void onPlayerJoin(PlayerJoinEvent event){
	    	Player player = event.getPlayer();
	        Bukkit.broadcastMessage("Welcome to the server "+player.getName()+"!");
	        if( storageType=="mysql" ) {
	        	//TODO
	        }else if( storageType=="mongodb" ){
	        	mongo.storePlayer(player, player.getName(), mongo.getPlayerTokens(player));
	        }else {
	        	sqllite.setTokens(player.getUniqueId(), sqllite.getTokens(player.getUniqueId()));
	        }
	    }
	    
	    @EventHandler
	    public void onPlayerQuit(PlayerQuitEvent event) {
	    	Player player = event.getPlayer();
	    	if( storageType=="mysql" ) {
	        	//TODO
	        }else if( storageType=="mongodb" ){
	        	mongo.storePlayer(player, player.getName(), mongo.getPlayerTokens(player));
	        }else {
	        	sqllite.setTokens(player.getUniqueId(), sqllite.getTokens(player.getUniqueId()));
	        }
	    }
   }
    
}
