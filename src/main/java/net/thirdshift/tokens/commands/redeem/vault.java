package net.thirdshift.tokens.commands.redeem;

import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.tokens.Tokens;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class vault {
    public static void redeemVault(Player player, int toRedeem, Tokens plugin){
        if (toRedeem <= plugin.handler.getTokens(player)){
            EconomyResponse r = plugin.getEconomy().depositPlayer(player, plugin.vaultSellPrice * (double)toRedeem);
            if (r.transactionSuccess()) {
                player.sendMessage(String.format("You have successfully redeemed " + ChatColor.GOLD + "" + toRedeem + "" + ChatColor.WHITE + " token(s) for "+ChatColor.GREEN+"%s", plugin.getEconomy().format(r.amount)));
                plugin.handler.setTokens(player, plugin.handler.getTokens(player) - toRedeem);
            } else {
                player.sendMessage(String.format("An error occurred: %s", r.errorMessage));
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't that many tokens to redeem!");
        }
    }

    public static void purchaseVault(Player player, int toRedeem, Tokens plugin){
        double price = plugin.vaultBuyPrice;
        double plyMoney = plugin.getEconomy().getBalance(player);
        double total = (double)toRedeem * price;
        if (total <= plyMoney) {
            EconomyResponse r = plugin.getEconomy().withdrawPlayer(player, total);
            if (r.transactionSuccess()) {
                player.sendMessage(String.format("You have successfully purchased " + ChatColor.GOLD + "" + toRedeem + "" + ChatColor.WHITE + " token(s) for %s" + ChatColor.GRAY, plugin.getEconomy().format(r.amount)));
                plugin.handler.addTokens(player, toRedeem);
            } else {
                player.sendMessage(ChatColor.RED+"There was an error withdrawing money from your account");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't have enough money to purchase that many tokens");
        }
    }

}
