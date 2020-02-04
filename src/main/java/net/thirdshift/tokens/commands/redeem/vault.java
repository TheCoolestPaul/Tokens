package net.thirdshift.tokens.commands.redeem;

import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.tokens.Tokens;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class vault {
    public static void redeemVault(Player player, int toRedeem, Tokens plugin){
        if (toRedeem <= plugin.handler.getTokens(player)){
            EconomyResponse r = Tokens.getEconomy().depositPlayer(player, plugin.vaultSellPrice * (double)toRedeem);
            if (r.transactionSuccess()) {
                player.sendMessage(String.format("You have successfully redeemed " + ChatColor.GOLD + "" + toRedeem + "" + ChatColor.WHITE + " token(s) for %s", Tokens.getEconomy().format(r.amount)));
                plugin.handler.setTokens(player, plugin.handler.getTokens(player) - toRedeem);
            } else {
                player.sendMessage(String.format("An error occurred: %s", r.errorMessage));
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't that many tokens to redeem!");
        }
    }
}
