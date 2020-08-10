package net.thirdshift.tokens.util;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.combatlogx.TokensCombatManager;
import net.thirdshift.tokens.commands.redeem.redeemcommands.FactionsRedeemModule;
import net.thirdshift.tokens.commands.redeem.redeemcommands.McMMORedeemModule;
import net.thirdshift.tokens.commands.redeem.redeemcommands.VaultRedeemModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TokensConfigHandler {
	private boolean mySQLEnabled = false;
	private boolean sqlliteEnabled = true;
	private boolean isRunningMySQL = false;

	private boolean hasFactions = false;
	private boolean factionsEnabled = false;
	private boolean isRunningFactions = false;
	private int tokenToFactionPower;

	private boolean hasMCMMO = false;
	private boolean mcmmoEnabled = false;
	private boolean isRunningMCMMO = false;
	private int tokensToMCMMOLevels;

	private boolean hasCombatLogX = false;
	private boolean combatLogXEnabled = false;
	private boolean combatLogXBlockTokens = false;
	private boolean isRunningCombatLogX = false;
	private TokensCombatManager tokensCombatManager;

	private boolean hasVault = false;
	private boolean vaultEnabled = false;
	private boolean vaultBuy = false;
	private boolean vaultSell = false;
	private boolean isRunningVault = false;
	private double vaultBuyPrice;
	private double vaultSellPrice;

	private final Tokens plugin;

	public TokensConfigHandler(final Tokens plugin){
		this.plugin = plugin;
	}

	public void reloadConfig(){
		this.mySQLEnabled = plugin.getConfig().getBoolean("MySQL.Enabled");

		// vault related config options
		vaultEnabled = plugin.getConfig().getBoolean("VaultEco.Enabled");
		vaultBuy = plugin.getConfig().getBoolean("VaultEco.Buy-Tokens");
		vaultBuyPrice = plugin.getConfig().getDouble("VaultEco.Buy-Price");
		vaultSell = plugin.getConfig().getBoolean("VaultEco.Sell-Tokens");
		vaultSellPrice = plugin.getConfig().getDouble("VaultEco.Sell-Price");

		// factions related config options
		factionsEnabled = plugin.getConfig().getBoolean("Factions.Enabled");
		tokenToFactionPower = plugin.getConfig().getInt("Factions.Tokens-To-Power");

		// combatlogx related config options
		combatLogXEnabled = plugin.getConfig().getBoolean("CombatLogX.Enabled");

		// mcmmo related config options
		this.mcmmoEnabled = plugin.getConfig().getBoolean("mcMMO.Enabled");
		this.tokensToMCMMOLevels = plugin.getConfig().getInt("mcMMO.Tokens-To-Levels");

		// MySQL Check
		if (mySQLEnabled) {
			if(plugin.getSqllite()!=null){
				plugin.nullSQLLite();
			}
			sqlliteEnabled = false;
			plugin.mySQLWork();
			isRunningMySQL = true;
			plugin.getLogger().info("Storage Type: SQLLite | [ MySQL ]");
		} else {
			if(plugin.getMySQL()!=null){
				plugin.getMySQL().stopSQLConnection();
				plugin.nullMySQL();
			}
			isRunningMySQL = false;
			sqlliteEnabled = true;
			plugin.doSQLLiteWork();
			plugin.getLogger().info("Storage Type: [ SQLLite ] | MySQL ( Default )");
		}

		// Factions Check
		if (factionsEnabled) {
			Plugin factionsPlug = Bukkit.getPluginManager().getPlugin("Factions");
			if (factionsPlug != null && factionsPlug.isEnabled()) {
				hasFactions = true;
				isRunningFactions = true;
				plugin.getRedeemCommandExecutor().registerRedeemModule(new FactionsRedeemModule());
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
				plugin.getRedeemCommandExecutor().registerRedeemModule(new VaultRedeemModule());
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
				hasCombatLogX = true;
				isRunningCombatLogX = true;
				if (tokensCombatManager==null)
					tokensCombatManager = new TokensCombatManager(this);
			} else if (combPlug == null || !combPlug.isEnabled()) {
				isRunningCombatLogX = false;
				plugin.getLogger().warning("CombatLogX addon is enabled but CombatLogX is not installed on the server!");
			}
		} else {
			isRunningCombatLogX = false;
		}

		// mcMMO Check
		if (mcmmoEnabled) {
			Plugin mcmmoPlug = Bukkit.getPluginManager().getPlugin("mcMMO");
			if (mcmmoPlug != null && mcmmoPlug.isEnabled()) {
				hasMCMMO = true;
				isRunningMCMMO = true;
				plugin.getRedeemCommandExecutor().registerRedeemModule(new McMMORedeemModule());
			} else if (mcmmoPlug == null || !mcmmoPlug.isEnabled()) {
				isRunningMCMMO = false;
				plugin.getLogger().warning("mcMMO addon is enabled but mcMMO is not installed on the server!");
			}
		} else {
			isRunningMCMMO = false;
		}

		// Prevents people like https://www.spigotmc.org/members/jcv.510317/ saying the plugin is broken <3
		if (!mcmmoEnabled && !factionsEnabled && !vaultEnabled) {
			plugin.getLogger().warning("You don't have any supported plugins enabled.");
		}
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

	public boolean isRunningCombatLogX() {
		return isRunningCombatLogX;
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

	public TokensCombatManager getTokensCombatManager() {
		return tokensCombatManager;
	}
}
