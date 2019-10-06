package main.java;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.LogFilter;
import main.java.commands.CMDRedeem;
import main.java.commands.CMDTokens;
import main.java.sqllite.Database;
import main.java.sqllite.SQLite;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Tokens extends JavaPlugin {
	boolean mysqlEnabled = false;
	private MySQLHandler mysql;
	private Database sqllite;
	public boolean hasFactions = false;
	public boolean hasMCMMO = false;
	public boolean hasCombatLogX = false;
	
	@Override
    public void onEnable() {
		getLogger().setFilter(new LogFilter(this));
		this.saveDefaultConfig();
		mysqlEnabled = this.getConfig().getBoolean("MySQL.Enabled");
		getServer().getPluginManager().registerEvents(new TokenListeners(), this);
		if( mysqlEnabled==true ) {
			MySQLHandler mysql = new MySQLHandler(); 
			System.out.println("Connecting to the database");
			mysql.username = this.getConfig().getString("MySQL.Username");
			mysql.password = this.getConfig().getString("MySQL.Password");
			mysql.dbName = this.getConfig().getString("MySQL.Database-Name");
			mysql.dbPORT = this.getConfig().getString("MySQL.Server.Port");
			mysql.dbAddress = this.getConfig().getString("MySQL.Server.Address");
			mysql.dbSSL = this.getConfig().getString("MySQL.Server.SSL");
			mysql.startSQLConnection();
		}else {
			this.sqllite = new SQLite(this);
	        this.sqllite.load();
		}
		
		Plugin factions = getServer().getPluginManager().getPlugin("Factions");
		if (factions != null && factions.isEnabled()) {
			getLogger().info("Hooked into Factions");
			hasFactions = true;
		}else {
			//getLogger().info("Factions was not found");
		}
		Plugin mcmmo = getServer().getPluginManager().getPlugin("mcMMO");
		if (mcmmo != null && mcmmo.isEnabled()) {
			getLogger().info("Hooked into mcMMO");
			hasMCMMO = true;
		}else {
			//getLogger().info("mcMMO was not found");
		}
		Plugin combatlogx = getServer().getPluginManager().getPlugin("CombatLogX");
		if (combatlogx != null && combatlogx.isEnabled()) {
			getLogger().info("Hooked into CombatLogX");
			hasCombatLogX = true;
		}else {
			//getLogger().info("mcMMO was not found");
		}
		
    	System.out.println("Enabling commands");
    	this.getCommand("tokens").setExecutor(new CMDTokens(this));
    	this.getCommand("redeem").setExecutor(new CMDRedeem(this));
    	
    }
	
    @Override
    public void onDisable() {
    	if( mysqlEnabled==true ) {
	    	System.out.println("Disconnecting from the database ");
	    	mysql.stopSQLConnection();
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
	        if( mysqlEnabled==true ) {
	        	//TODO
	        }else {
	        	sqllite.setTokens(player.getUniqueId(), sqllite.getTokens(player.getUniqueId()));
	        }
	    }
	    
	    @EventHandler
	    public void onPlayerQuit(PlayerQuitEvent event) {
	    	Player player = event.getPlayer();
	    	if( mysqlEnabled==true ) {
	        	//TODO
	        }else {
	        	sqllite.setTokens(player.getUniqueId(), sqllite.getTokens(player.getUniqueId()));
	        }
	    }
   }
    
}
