package net.thirdshift.tokens.commands.redeem;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.thirdshift.tokens.Tokens;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class factions {
    public static void redeemFactions(Player player, int toRedeem, Tokens plugin){
        if (toRedeem <= plugin.handler.getTokens(player)) {
            FPlayer facPly = FPlayers.getInstance().getByPlayer(player);
            if (facPly != null) {
                facPly.setPowerBoost(facPly.getPowerBoost() + (double)(toRedeem * plugin.tokenToFactionPower));
                plugin.handler.setTokens(player, plugin.handler.getTokens(player) - toRedeem);
                player.sendMessage("You redeemed " + ChatColor.GOLD + "" + toRedeem + " token(s)");
                player.sendMessage("Your maximum faction power is now " + ChatColor.GREEN + "" + facPly.getPowerMax());
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't that many tokens to redeem!");
        }
    }
}
