package net.thirdshift.tokens.util;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.combatlogx.TokensCombatManager;
import net.thirdshift.tokens.commands.redeem.redeemcommands.FactionsRedeemCommandModule;
import net.thirdshift.tokens.commands.redeem.redeemcommands.McMMORedeemCommandModule;
import net.thirdshift.tokens.commands.redeem.redeemcommands.VaultRedeemCommandModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TokensConfigHandler {
	private boolean mySQLEnabled = false;
	private boolean SQLiteEnabled = true;
	private boolean isRunningMySQL = false;

	private boolean hasFactions = false;
	private boolean factionsEnabled = false;
	private boolean isRunningFactions = false;
	private int tokenToFactionPower;

	private boolean hasMCMMO = false;
	private boolean mcmmoEnabled = false;
	private boolean isRunningMCMMO = false;
	private int tokensToMCMMOLevels;

	private boolean combatLogXEnabled = false;

	private boolean hasVault = false;
	private boolean vaultEnabled = false;
	private boolean vaultBuy = false;
	private boolean vaultSell = false;
	private boolean isRunningVault = false;
	private double vaultBuyPrice;
	private double vaultSellPrice;

	private boolean updateCheck = false;
	private int hoursToCheck = 5;

	private final Tokens plugin;

	public TokensConfigHandler(final Tokens plugin){
		this.plugin = plugin;
	}

	public void reloadConfig(){
		this.mySQLEnabled = plugin.getConfig().getBoolean("MySQL.Enabled");

		// vault related config options
		vaultEnabled = plugin.getConfig().getBoolean("VaultEco.Enabled", false);
		vaultBuy = plugin.getConfig().getBoolean("VaultEco.Buy-Tokens", false);
		vaultBuyPrice = plugin.getConfig().getDouble("VaultEco.Buy-Price", 1000);
		vaultSell = plugin.getConfig().getBoolean("VaultEco.Sell-Tokens", false);
		vaultSellPrice = plugin.getConfig().getDouble("VaultEco.Sell-Price", 1000);

		// factions related config options
		factionsEnabled = plugin.getConfig().getBoolean("Factions.Enabled", false);
		tokenToFactionPower = plugin.getConfig().getInt("Factions.Tokens-To-Power", 1);

		// CombatLogX related config options
		combatLogXEnabled = plugin.getConfig().getBoolean("CombatLogX.Enabled", false);

		// mcmmo related config options
		mcmmoEnabled = plugin.getConfig().getBoolean("mcMMO.Enabled", false);
		tokensToMCMMOLevels = plugin.getConfig().getInt("mcMMO.Tokens-To-Levels", 1);

		// Update-check
		updateCheck = plugin.getConfig().getBoolean("UpdateCheck.Enabled", true);
		hoursToCheck = plugin.getConfig().getInt("UpdateCheck.Interval", 5);

		// MySQL Check
		if (mySQLEnabled) {
			if(plugin.getSqllite()!=null){
				plugin.nullSQLLite();
			}
			SQLiteEnabled = false;
			plugin.mySQLWork();
			isRunningMySQL = true;
			plugin.getLogger().info("Storage Type: SQLLite | [ MySQL ]");
		} else {
			if(plugin.getMySQL()!=null){
				plugin.getMySQL().closeConnection();
				plugin.nullMySQL();
			}
			isRunningMySQL = false;
			SQLiteEnabled = true;
			plugin.doSQLLiteWork();
			plugin.getLogger().info("Storage Type: [ SQLLite ] | MySQL ( Default )");
		}

		// Factions Check
		if (factionsEnabled) {
			Plugin factionsPlug = Bukkit.getPluginManager().getPlugin("Factions");
			if (factionsPlug != null && factionsPlug.isEnabled()) {
				hasFactions = true;
				isRunningFactions = true;
				plugin.getRedeemCommandExecutor().registerModule(new FactionsRedeemCommandModule(plugin.getRedeemCommandExecutor()));
			} else if (factionsPlug == null || !factionsPlug.isEnabled()) {
				plugin.getLogger().warning("Factions addon is enabled but Factions is not installed on the server!");
				isRunningFactions = false;
			}
		} else {
			isRunningFactions = false;
		}

		// Vault Check
		if (vaultEnabled) {
			Plugin vaultPlug = Bukkit.getPluginManager().getPlugin("Vault");
			if (vaultPlug != null && vaultPlug.isEnabled()) {
				hasVault = true;
				plugin.getRedeemCommandExecutor().registerModule(new VaultRedeemCommandModule(plugin.getRedeemCommandExecutor()));
				plugin.vaultIntegration();
			} else if (vaultPlug == null || !vaultPlug.isEnabled()) {
				isRunningVault = false;
				plugin.getLogger().warning("Vault addon is enabled but Vault is not installed on the server!");
			}
		} else {
			isRunningVault = false;
		}

		// CombatLogX Check
		if (combatLogXEnabled) {
			Plugin combPlug = Bukkit.getPluginManager().getPlugin("CombatLogX");
			if (combPlug != null && combPlug.isEnabled()) {
				plugin.setTokensCombatManager(new TokensCombatManager(combPlug));
			}
		}

		// mcMMO Check
		if (mcmmoEnabled) {
			Plugin mcmmoPlug = Bukkit.getPluginManager().getPlugin("mcMMO");
			if (mcmmoPlug != null && mcmmoPlug.isEnabled()) {
				hasMCMMO = true;
				isRunningMCMMO = true;
				plugin.getRedeemCommandExecutor().registerModule(new McMMORedeemCommandModule(plugin.getRedeemCommandExecutor()));
			} else if (mcmmoPlug == null || !mcmmoPlug.isEnabled()) {
				isRunningMCMMO = false;
				plugin.getLogger().warning("mcMMO addon is enabled but mcMMO is not installed on the server!");
			}
		} else {
			isRunningMCMMO = false;
		}

		// Prevents people like https://www.spigotmc.org/members/jcv.510317/ saying the plugin is broken <3
		if (!mcmmoEnabled && !factionsEnabled && !vaultEnabled && !plugin.getHookManager().HasConsumable()) {
			plugin.getLogger().warning("You don't have any supported plugins enabled.");
		}
	}

	public boolean isUpdateCheck() {
		return updateCheck;
	}

	public void setUpdateCheck(boolean updateCheck) {
		this.updateCheck = updateCheck;
	}

	public int getHoursToCheck() {
		return hoursToCheck;
	}

	public void setHoursToCheck(int hoursToCheck) {
		this.hoursToCheck = hoursToCheck;
	}

	public boolean isRunningMySQL(){
		return mySQLEnabled;
	}

	public boolean isRunningMCMMO() {
		return isRunningMCMMO;
	}

	public int getTokensToMCMMOLevels() {
		return tokensToMCMMOLevels;
	}

	public boolean isRunningVault() {
		return isRunningVault;
	}

	public boolean isRunningFactions() {
		return isRunningFactions;
	}

	public int getTokenToFactionPower() {
		return tokenToFactionPower;
	}

	public boolean isVaultBuy() {
		return vaultBuy;
	}

	public boolean isVaultSell() {
		return vaultSell;
	}

	public double getVaultBuyPrice() {
		return vaultBuyPrice;
	}

	public double getVaultSellPrice() {
		return vaultSellPrice;
	}

}
