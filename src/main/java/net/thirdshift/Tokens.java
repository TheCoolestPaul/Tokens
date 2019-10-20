package net.thirdshift;

import net.milkbowl.vault.economy.Economy;
import net.thirdshift.commands.CMDRedeem;
import net.thirdshift.commands.CMDTokens;
import net.thirdshift.database.mysql.MySQLHandler;
import net.thirdshift.database.sqllite.Database;
import net.thirdshift.database.sqllite.SQLite;
import net.thirdshift.util.SpigotUpdater;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class Tokens extends JavaPlugin {
    public boolean mysqlEnabled = false;
    private MySQLHandler mysql;
    private Database sqllite;
    public boolean hasFactions, factionsEnabled = false;
    public int tokenToFactionPower;
    public boolean hasMCMMO, mcmmoEnabled = false;
    public int tokensToMCMMOLevels;
    public boolean hasCombatLogX, combatLogXEnabled, combatLogXBlockTokens = false;
    public boolean hasVault, vaultEnabled, vaultBuy, vaultSell = false;
    public double vaultBuyPrice, vaultSellPrice = 0.0;
    public static Economy economy;
    private SpigotUpdater updater = new SpigotUpdater(this, 71941);

    @Override
    public void onEnable() {
        getLogger().setFilter(new LogFilter(this));
        checkForUpdates();
        this.saveDefaultConfig();
        mysqlEnabled = this.getConfig().getBoolean("MySQL.Enabled");
        if(mysqlEnabled) {
            this.mysql = new MySQLHandler(this);
            mysql.username = this.getConfig().getString("MySQL.Username");
            mysql.password = this.getConfig().getString("MySQL.Password");
            mysql.dbName = this.getConfig().getString("MySQL.Database-Name");
            mysql.dbPORT = this.getConfig().getString("MySQL.Server.Port");
            mysql.dbAddress = this.getConfig().getString("MySQL.Server.Address");
            mysql.dbSSL = this.getConfig().getString("MySQL.Server.SSL");
            mysql.startSQLConnection();
            getLogger().info("Using MySQL");
        }else {
            this.sqllite = new SQLite(this);
            this.sqllite.load();
            getLogger().info("Using SQLlite");
        }

        Plugin factions = getServer().getPluginManager().getPlugin("Factions");
        if (factions != null && factions.isEnabled()) {
            getLogger().info("Hooked into Factions");
            hasFactions = true;
            factionsEnabled = this.getConfig().getBoolean("Factions.Enabled");
            tokenToFactionPower = this.getConfig().getInt("Factions.Tokens-To-Power");
        }else if (this.getConfig().getBoolean("Factions.Enabled")){
            getLogger().info("Factions is enabled but not installed!");
        }

        Plugin mcmmo = getServer().getPluginManager().getPlugin("mcMMO");
        if (mcmmo != null && mcmmo.isEnabled()) {
            getLogger().info("Hooked into mcMMO");
            hasMCMMO = true;
            mcmmoEnabled = this.getConfig().getBoolean("mcMMO.Enabled");
            tokensToMCMMOLevels = this.getConfig().getInt("mcMMO.Tokens-To-Levels");
        }else if(this.getConfig().getBoolean("mcMMO.Enabled")){
            getLogger().warning("mcMMO is enabled but not installed!");
        }

        Plugin combatlogx = getServer().getPluginManager().getPlugin("CombatLogX");
        if (combatlogx != null && combatlogx.isEnabled()) {
            getLogger().info("Hooked into CombatLogX");
            hasCombatLogX = true;
            combatLogXEnabled = this.getConfig().getBoolean("CombatLogX.Enabled");
            combatLogXBlockTokens = this.getConfig().getBoolean("CombatLogX.Block-Tokens");
        }else if(this.getConfig().getBoolean("CombatLogX.Enabled")) {
            getLogger().warning("CombatLogX is enabled but not installed!");
        }

        Plugin valutplug = getServer().getPluginManager().getPlugin("Vault");
        if(valutplug != null && valutplug.isEnabled()){
            boolean maybe = setupEconomy();
            if(!maybe) {
                hasVault = false;
                getLogger().warning("Vault doesn't have an economy plugin!");
            }else{
                getLogger().info("Hooked into Vault Economy");
                hasVault = true;
                vaultEnabled = this.getConfig().getBoolean("VaultEco.Enabled");
                vaultBuy = this.getConfig().getBoolean("VaultEco.Buy-Tokens");
                vaultBuyPrice = this.getConfig().getDouble("VaultEco.Buy-Price");
                vaultSell = this.getConfig().getBoolean("VaultEco.Sell-Tokens");
                vaultSellPrice = this.getConfig().getDouble("VaultEco.Sell-Price");
            }
        }else if(this.getConfig().getBoolean("VaultEco.Enabled")){
            getLogger().warning("Vault is enabled but not installed!");
        }
        if(!hasFactions&&!hasMCMMO&&!hasCombatLogX&&!hasVault){
            getLogger().info("You don't have any supported plugins installed");
            this.setEnabled(false);
        }
        getLogger().info("Enabling commands");
        this.getCommand("tokens").setExecutor(new CMDTokens(this));
        this.getCommand("redeem").setExecutor(new CMDRedeem(this));

    }

    @Override
    public void onDisable() {
        if( mysqlEnabled ) {
            getLogger().info("Disconnecting from MySQL");
            mysql.stopSQLConnection();
        }
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public int getTokens(Player player){
        if(mysqlEnabled){
            return mysql.getTokens(player);
        }else{
            return sqllite.getTokens(player.getUniqueId());
        }
    }

    public void setTokens(Player player, int tokens){
        if(mysqlEnabled){
            mysql.setTokens(player, tokens);
        }else{
            sqllite.setTokens(player.getUniqueId(), tokens);
        }
    }

    public void addTokens(Player player, int tokens){
        int curTokens;
        if(mysqlEnabled){
            curTokens = mysql.getTokens(player);
            mysql.setTokens(player, tokens+curTokens);
        }else{
            curTokens = sqllite.getTokens(player.getUniqueId());
            sqllite.setTokens(player.getUniqueId(), curTokens+tokens);
        }
    }

    private void checkForUpdates(){
        try {
            if (updater.checkForUpdates()) {
                getLogger().info("An update was found! New version: " + updater.getLatestVersion() + " download: " + updater.getResourceURL());
            }else{
                getLogger().info("is up to date!");
            }
        } catch (Exception e) {
            getLogger().warning("Could not check for updates! Stacktrace:");
            e.printStackTrace();
        }
    }
}
