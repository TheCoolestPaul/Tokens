package net.thirdshift.tokens;

import net.milkbowl.vault.economy.Economy;
import net.thirdshift.tokens.cache.TokenCache;
import net.thirdshift.tokens.combatlogx.TokensCombatManager;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.commands.redeem.RedeemCommandExecutor;
import net.thirdshift.tokens.commands.redeem.TabRedeem;
import net.thirdshift.tokens.commands.tokens.TabTokens;
import net.thirdshift.tokens.commands.tokens.TokensCommandExecutor;
import net.thirdshift.tokens.commands.tokens.tokenscommands.*;
import net.thirdshift.tokens.database.mysql.MySQLHandler;
import net.thirdshift.tokens.database.sqllite.SQLLite;
import net.thirdshift.tokens.hooks.TokensBaseHooks;
import net.thirdshift.tokens.hooks.TokensHookManager;
import net.thirdshift.tokens.keys.KeyHandler;
import net.thirdshift.tokens.messages.MessageHandler;
import net.thirdshift.tokens.util.Metrics;
import net.thirdshift.tokens.util.TokensConfigHandler;
import net.thirdshift.tokens.util.TokensUpdateEventListener;
import net.thirdshift.tokens.util.updater.TokensSpigotUpdater;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Tokens extends JavaPlugin {
	private TokensConfigHandler tokensConfigHandler;

	private Metrics tokensMetrics;

	private MySQLHandler mysql;
	private SQLLite sqllite;

	public static Economy vaultEcon;

	private TokensUpdateEventListener tokensUpdateEventListener;
	private final TokensSpigotUpdater updater = new TokensSpigotUpdater(this, 71941);
	private FileConfiguration keyConfig = null;
	private File keyFile = null;
	public KeyHandler keyHandler;

	public MessageHandler messageHandler;
	private File messageFile = null;
	private FileConfiguration messageConfig = null;

	private PluginCommand tokensCommand;
	private PluginCommand redeemCommand;
	private TokensHandler tokensHandler;

	private TokensCombatManager tokensCombatManager;

	private RedeemCommandExecutor redeemCommandExecutor;
	private TokensCommandExecutor tokensCommandExecutor;

	private TokensHookManager hookManager;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		hookManager = new TokensHookManager(this);

		tokensConfigHandler = new TokensConfigHandler(this);
		keyHandler = new KeyHandler(this);

		tokensUpdateEventListener = new TokensUpdateEventListener(this);
		getServer().getPluginManager().registerEvents(tokensUpdateEventListener, this);

		tokensHandler = new TokensHandler();

		messageHandler = new MessageHandler(this);
		messageHandler.loadMessages();

		tokensCommand = this.getCommand("tokens");
		redeemCommand = this.getCommand("redeem");

		redeemCommandExecutor = new RedeemCommandExecutor(this);
		tokensCommandExecutor = new TokensCommandExecutor(this);

		TokenCache.initialize( this );

		tokensMetrics = new Metrics(this, 5849);

		this.workCommands();
		this.reloadConfig();

		// Auto-check updates related code
		if (getTokensConfigHandler().isUpdateCheck()) {
			Runnable runnable = this::checkUpdates;
			final int task = getServer().getScheduler().scheduleSyncRepeatingTask(this, runnable, 0, 20 * ((long) getTokensConfigHandler().getHoursToCheck() * 60 * 60));
			if (task == -1) {
				getLogger().severe("Couldn't schedule an auto-update check.");
			} else {
				getLogger().info("Will check for updates every " +getTokensConfigHandler().getHoursToCheck()+" hours.");
			}
		}

		TokensBaseHooks.registerBaseHooks(this, hookManager);
	}

	public TokensHookManager getHookManager() {
		return hookManager;
	}

	public TokensConfigHandler getTokensConfigHandler() {
		return tokensConfigHandler;
	}

	public Metrics getTokensMetrics() {
		return tokensMetrics;
	}

	public void setTokensMetrics(Metrics tokensMetrics) {
		this.tokensMetrics = tokensMetrics;
	}

	public TokensUpdateEventListener getTokensEventListener() {
		return tokensUpdateEventListener;
	}

	public TokensCombatManager getTokensCombatManager() {
		return tokensCombatManager;
	}

	public void setTokensCombatManager(TokensCombatManager tokensCombatManager) {
		this.tokensCombatManager = tokensCombatManager;
	}

	public TokensHandler getHandler() {
		return tokensHandler;
	}

	@Override
	public void onDisable() {

		TokenCache.onDisable();
		keyHandler.saveKeyCooldown();

		// The database connections are shutdown in TokenCacheDatabase but
		// providing final attempts here just to ensure they are shutdown to
		// help prevent corruption.
		if(tokensConfigHandler.isRunningMySQL()){
			mysql.closeConnection();
		}
		else {
			sqllite.closeConnection();
		}
	}

	public void workCommands(){
		tokensCommand.setExecutor(tokensCommandExecutor);
		addTokensCommandsModules(tokensCommandExecutor);
		redeemCommand.setExecutor(redeemCommandExecutor);

		tokensCommand.setTabCompleter(new TabTokens(this));
		redeemCommand.setTabCompleter(new TabRedeem(this));
	}

	public void addTokensCommandsModules(TokensCustomCommandExecutor executor){
		executor.registerModule( new AddTokensCommandModule(executor) );

		if (this.getTokensConfigHandler().isVaultBuy())
			executor.registerModule( new BuyTokensCommandModule(executor) );

		executor.registerModule( new GiveTokensCommandModule(executor) );
		executor.registerModule( new HelpTokensCommandModule(executor) );
		executor.registerModule( new KeyTokensCommandModule(executor) );
		executor.registerModule( new ReloadTokensCommandModule(executor) );
		executor.registerModule( new RemoveTokensCommandModule(executor) );
		executor.registerModule( new SetTokensCommandModule(executor) );
		executor.registerModule( new CacheTokensCommandModule(executor) );
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
	}

	public void doSQLLiteWork(){
		this.sqllite = new SQLLite(this);
		this.sqllite.load();
	}

	public void mySQLWork(){
		if(this.mysql==null) {
			this.mysql = new MySQLHandler(this);
		}else{
			this.mysql.closeConnection();
			this.getLogger().fine("Closing old MySQL connection.");
		}
		this.mysql.updateSettings();
		this.getLogger().fine("Updated MySQL connection.");
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
		this.getLogger().info("Checking for updates");
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

	public TokensCommandExecutor getTokensCommandExecutor() {
		return tokensCommandExecutor;
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
