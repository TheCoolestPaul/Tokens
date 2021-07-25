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

	private int tokenToFactionPower;

	private boolean isRunningMCMMO = false;
	private int tokensToMCMMOLevels;

	private boolean vaultBuy = false;
	private double vaultBuyPrice;
	private double vaultSellPrice;

	private boolean negTokens = false;
	private boolean updateCheck = false;
	private int hoursToCheck = 5;

	private final Tokens plugin;

	public TokensConfigHandler(final Tokens plugin){
		this.plugin = plugin;
	}

	public void reloadConfig(){
		// Tokens General Settings
		negTokens = plugin.getConfig().getBoolean("Tokens.Negative-Balances-Enabled", false);
		updateCheck = plugin.getConfig().getBoolean("Tokens.UpdateCheck.Enabled", true);
		hoursToCheck = plugin.getConfig().getInt("Tokens.UpdateCheck.Interval", 6);


		mySQLEnabled = plugin.getConfig().getBoolean("MySQL.Enabled");

		// vault related config options
		boolean vaultEnabled = plugin.getConfig().getBoolean("VaultEco.Enabled", false);
		vaultBuy = plugin.getConfig().getBoolean("VaultEco.Buy-Tokens", false);
		vaultBuyPrice = plugin.getConfig().getDouble("VaultEco.Buy-Price", 1000);
		boolean vaultSell = plugin.getConfig().getBoolean("VaultEco.Sell-Tokens", false);
		vaultSellPrice = plugin.getConfig().getDouble("VaultEco.Sell-Price", 1000);

		// factions related config options
		boolean factionsEnabled = plugin.getConfig().getBoolean("Factions.Enabled", false);
		tokenToFactionPower = plugin.getConfig().getInt("Factions.Tokens-To-Power", 1);

		// CombatLogX related config options
		boolean combatLogXEnabled = plugin.getConfig().getBoolean("CombatLogX.Enabled", false);

		// mcmmo related config options
		boolean mcmmoEnabled = plugin.getConfig().getBoolean("mcMMO.Enabled", false);
		tokensToMCMMOLevels = plugin.getConfig().getInt("mcMMO.Tokens-To-Levels", 1);

		// MySQL Check
		if (mySQLEnabled) {
			if(plugin.getSqllite()!=null){
				plugin.nullSQLLite();
			}
			plugin.mySQLWork();
			plugin.getLogger().info("Storage Type: SQLLite | [ MySQL ]");
		} else {
			if(plugin.getMySQL()!=null){
				plugin.getMySQL().closeConnection();
				plugin.nullMySQL();
			}
			plugin.doSQLLiteWork();
			plugin.getLogger().info("Storage Type: [ SQLLite ] | MySQL ( Default )");
		}

		// Factions Check
		if (factionsEnabled) {
			Plugin factionsPlug = Bukkit.getPluginManager().getPlugin("Factions");
			if (factionsPlug != null && factionsPlug.isEnabled()) {
				plugin.getRedeemCommandExecutor().registerModule(new FactionsRedeemCommandModule(plugin.getRedeemCommandExecutor()));
			} else if (factionsPlug == null || !factionsPlug.isEnabled()) {
				plugin.getLogger().warning("Factions addon is enabled but Factions is not installed on the server!");
			}
		}

		// Vault Check
		if (vaultEnabled) {
			Plugin vaultPlug = Bukkit.getPluginManager().getPlugin("Vault");
			if (vaultPlug != null && vaultPlug.isEnabled() && vaultSell) {
				plugin.getRedeemCommandExecutor().registerModule(new VaultRedeemCommandModule(plugin.getRedeemCommandExecutor()));
				plugin.vaultIntegration();
			} else if (vaultPlug == null || !vaultPlug.isEnabled()) {
				plugin.getLogger().warning("Vault addon is enabled but Vault is not installed on the server!");
			}
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

	public int getHoursToCheck() {
		return hoursToCheck;
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

	public int getTokenToFactionPower() {
		return tokenToFactionPower;
	}

	public boolean isVaultBuy() {
		return vaultBuy;
	}

	public double getVaultBuyPrice() {
		return vaultBuyPrice;
	}

	public double getVaultSellPrice() {
		return vaultSellPrice;
	}

	public boolean negativeTokens(){
		return negTokens;
	}

}
