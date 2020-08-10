package net.thirdshift.tokens.combatlogx;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import net.thirdshift.tokens.util.TokensConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TokensCombatManager {
	ICombatManager combatManager;
	TokensConfigHandler configHandler;
	public TokensCombatManager(final TokensConfigHandler configHandler){
		combatManager = ((ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX")).getCombatManager();
		this.configHandler = configHandler;
	}

	public boolean isInCombat(Player player){
		if (configHandler.isRunningCombatLogX())
			return combatManager.isInCombat(player);
		return false;
	}
}
