package net.thirdshift.tokens;

import net.milkbowl.vault.economy.Economy;
import net.thirdshift.tokens.commands.redeem.RedeemCommandExecutor;
import net.thirdshift.tokens.commands.redeem.redeemcommands.KeyRedeemModule;
import net.thirdshift.tokens.commands.tokens.CommandTokens;
import net.thirdshift.tokens.commands.redeem.TabRedeem;
import net.thirdshift.tokens.commands.tokens.TabTokens;
import net.thirdshift.tokens.database.mysql.MySQLHandler;
import net.thirdshift.tokens.database.sqllite.SQLLite;
import net.thirdshift.tokens.keys.KeyHandler;
import net.thirdshift.tokens.messages.MessageHandler;
import net.thirdshift.tokens.shopguiplus.TokenShopGUIPlus;
import net.thirdshift.tokens.util.BStats;
import net.thirdshift.tokens.util.TokensConfigHandler;
import net.thirdshift.tokens.util.TokensEventListener;
import net.thirdshift.tokens.util.TokensPAPIExpansion;
import net.thirdshift.tokens.util.updater.TokensSpigotUpdater;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Tokens extends JavaPlugin {

	private static Tokens instance;

	private TokensConfigHandler tokensConfigHandler;

	private MySQLHandler mysql;
	private SQLLite sqllite;

	public static Economy vaultEcon;

	private final TokensEventListener tokensEventListener = new TokensEventListener(this);
	private final TokensSpigotUpdater updater = new TokensSpigotUpdater(this, 71941);
	private FileConfiguration keyConfig = null;
	private File keyFile = null;
	public KeyHandler keyHander;

	public MessageHandler messageHandler;
	private File messageFile = null;
	private FileConfiguration messageConfig = null;

	private PluginCommand tokensCommand;
	private PluginCommand redeemCommand;
	private TokensHandler tokensHandler;
	private TokenShopGUIPlus tokenShopGUIPlus;
	private RedeemCommandExecutor redeemCommandExecutor;

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		tokensConfigHandler = new TokensConfigHandler(this);

		getServer().getPluginManager().registerEvents(tokensEventListener, this);
		tokensHandler = new TokensHandler(this);
		keyHander = new KeyHandler(this);
		messageHandler = new MessageHandler(this);

		messageHandler.loadMessages();

		tokensCommand = this.getCommand("tokens");
		redeemCommand = this.getCommand("redeem");

		redeemCommandExecutor = new RedeemCommandExecutor(this);

		new BStats(this, 5849);

		this.workCommands();
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null){
			boolean tokensExpansion = new TokensPAPIExpansion(this).register();
			if(tokensExpansion) {
				this.getLogger().info("Successfully registered into PlaceholderAPI");
			}else{
				this.getLogger().warning("Couldn't register into PlaceholderAPI");
			}
		}
		if(Bukkit.getPluginManager().getPlugin("ShopGUIPlus")!=null){
			tokenShopGUIPlus = new TokenShopGUIPlus(this);
			this.getLogger().info("Successfully registered Tokens as ShopGUI+ economy");
		}
		this.reloadConfig();

		// Auto-check updates related code
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				checkUpdates();
			}
		};
		// Initial check for updates, then schedule one once every 20 minutes
		final int task = getServer().getScheduler().scheduleSyncRepeatingTask(this, runnable, 0, 24000);
		if (task==-1){
			getLogger().warning("Couldn't schedule an auto-update check!");
		}
	}

	public TokensConfigHandler getTokensConfigHandler() {
		return tokensConfigHandler;
	}

	public TokensEventListener getTokensEventListener() {
		return tokensEventListener;
	}

	public static Tokens getInstance(){
		return instance;
	}

	public TokenShopGUIPlus getTokenShopGUIPlus() {
		return tokenShopGUIPlus;
	}

	public TokensHandler getHandler() {
		return tokensHandler;
	}

	@Override
	public void onDisable() {
		keyHander.saveKeyCooldown();
		instance = null;
		if(tokensConfigHandler.isRunningMySQL()){
			mysql.stopSQLConnection();//Cut off any loose bois
		}
	}

	public void workCommands(){
		tokensCommand.setExecutor(new CommandTokens(this));
		redeemCommand.setExecutor(redeemCommandExecutor);

		tokensCommand.setTabCompleter(new TabTokens(this));
		redeemCommand.setTabCompleter(new TabRedeem(this));
	}

	public void reloadKeys() {
		if (keyFile == null) {
			keyFile = new File(getDataFolder(), "keys.yml");
		}
		keyConfig = YamlConfiguration.loadConfiguration(keyFile);

		// Look for defaults in the jar
		InputStreamReader stream = null;
		Reader defConfigStream = null;
		try{
			stream = new InputStreamReader(Objects.requireNonNull(this.getResource("keys.yml")), StandardCharsets.UTF_8);
			defConfigStream = stream;
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			keyConfig.setDefaults(defConfig);
		}finally{
			try{
				if(stream!=null)
					stream.close();
				if(defConfigStream!=null)
					defConfigStream.close();
			}catch(IOException ex){
				this.getLogger().severe("Error reading keys.yml");
			}
		}
	}

	public void reloadMessages() {
		if (messageFile == null) {
			messageFile = new File(getDataFolder(), "messages.yml");
		}
		messageConfig = YamlConfiguration.loadConfiguration(messageFile);

		// Look for defaults in the jar
		InputStreamReader stream = null;
		Reader defConfigStream = null;
		try{
			stream = new InputStreamReader(Objects.requireNonNull(this.getResource("messages.yml")), StandardCharsets.UTF_8);
			defConfigStream = stream;
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			messageConfig.setDefaults(defConfig);
		}finally{
			try{
				if(stream!=null)
					stream.close();
				if(defConfigStream!=null)
					defConfigStream.close();
			}catch(IOException ex){
				this.getLogger().severe("Error reading keys.yml");
			}
		}
		messageHandler.loadMessages();
	}

	public FileConfiguration getMessageConfig(){
		if (messageConfig == null){
			reloadMessages();
		}
		return messageConfig;
	}

	public FileConfiguration getKeyConfig() {
		if (keyConfig == null) {
			reloadKeys();
		}
		return keyConfig;
	}

	@Override
	public void saveDefaultConfig() {
		super.saveDefaultConfig();
		if (keyFile == null) {
			keyFile = new File(getDataFolder(), "keys.yml");
		}
		if (!keyFile.exists()) {
			saveResource("keys.yml", false);
		}
		if (messageFile == null){
			messageFile = new File(getDataFolder(), "messages.yml");
		}
		if (!messageFile.exists()){
			saveResource("messages.yml", false);
		}
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		tokensConfigHandler.reloadConfig();
		redeemCommandExecutor.registerRedeemModule(new KeyRedeemModule());
	}

	public void doSQLLiteWork(){
		this.sqllite = new SQLLite(this);
		this.sqllite.load();
	}

	public void mySQLWork(){
		if(this.mysql==null) {
			this.mysql = new MySQLHandler(this);
		}else{
			this.mysql.stopSQLConnection();
			this.getLogger().info("Closing old MySQL connection.");
		}
		this.mysql.updateSettings();
		this.getLogger().info("Updated MySQL connection.");
		this.mysql.startSQLConnection();
	}

	public void vaultIntegration(){
		if(vaultEcon == null){
			if(!setupEconomy()){
				this.getLogger().warning("No Vault economy is present but the addon is enabled!");
			}
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		vaultEcon = rsp.getProvider();
		return true;
	}

	public SQLLite getSqllite() {
		return sqllite;
	}

	public MySQLHandler getMySQL() {
		return mysql;
	}

	public Economy getEconomy() {
		return vaultEcon;
	}

	private void checkUpdates(){
		try {
			if (updater.checkForUpdates()) {
				this.getLogger().info("An update was found! New version: " + updater.getLatestVersion() + " download link: " + updater.getResourceURL());
			} else {
				this.getLogger().info("No newer version available.");
			}
		} catch (Exception var2) {
			this.getLogger().warning("Could not check for updates! Stacktrace:");
			var2.printStackTrace();
		}
	}

	public RedeemCommandExecutor getRedeemCommandExecutor() {
		return redeemCommandExecutor;
	}

	public void nullMySQL() {
		mysql = null;
	}

	public void nullSQLLite() {
		sqllite = null;
	}
}
