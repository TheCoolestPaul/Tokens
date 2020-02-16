package net.thirdshift.tokens.commands.redeem;

import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class vault {
    public static void redeemVault(Player player, int toRedeem, Tokens plugin){
        if (toRedeem <= plugin.handler.getTokens(player)){
            double money = plugin.vaultSellPrice*toRedeem;
            List<Object> objects = new ArrayList<>();
            objects.add(money);
            objects.add(new PlayerSender(player));
            objects.add(toRedeem);
            EconomyResponse r = plugin.getEconomy().depositPlayer(player, money);
            if (r.transactionSuccess()) {
                plugin.handler.setTokens(player, plugin.handler.getTokens(player) - toRedeem);
                player.sendMessage(plugin.messageHandler.useMessage("redeem.vault.sell", objects));
            } else {
                player.sendMessage(String.format("An error occurred: %s", r.errorMessage));
            }
        } else {
            List<Object> objects = new ArrayList<>();
            objects.add(toRedeem);
            objects.add(new PlayerSender(player));
            player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", objects));
        }
    }

    public static void purchaseVault(Player player, int toRedeem, Tokens plugin){
        double price = plugin.vaultBuyPrice;
        double plyMoney = plugin.getEconomy().getBalance(player);
        double total = (double)toRedeem * price;
        List<Object> objects = new ArrayList<>();
        objects.add(total);
        objects.add(toRedeem);
        objects.add(new PlayerSender(player));
        if (total <= plyMoney) {
            EconomyResponse r = plugin.getEconomy().withdrawPlayer(player, total);
            if (r.transactionSuccess()) {
                player.sendMessage(plugin.messageHandler.useMessage("redeem.vault.buy", objects));
                plugin.handler.addTokens(player, toRedeem);
            } else {
                player.sendMessage(ChatColor.RED+"There was an error withdrawing money from your account");
            }
        } else {
            player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.no-money", objects));
        }
    }

}
