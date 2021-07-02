package net.thirdshift.tokens.combatlogx;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TokensCombatManager {
	ICombatManager combatManager;
	public TokensCombatManager(Plugin combatPlugin){
		ICombatLogX combatLog =  (ICombatLogX) combatPlugin;
		combatManager = combatLog.getCombatManager();
	}

	public boolean isInCombat(Player player){
		return combatManager.isInCombat(player);
	}
}
