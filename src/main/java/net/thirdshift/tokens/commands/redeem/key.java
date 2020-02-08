package net.thirdshift.tokens.commands.redeem;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.keys.Key;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class key {
    public static void redeemKey(Player player, String keyName, Tokens plugin){
        if(plugin.keyHander.isValidKey(keyName)){
            Key key = plugin.keyHander.getKey(keyName);
            long timeLeft = System.currentTimeMillis() - key.getPlayerCooldown(player);
            if(TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= TimeUnit.MINUTES.toSeconds(key.getCooldown())){
                player.sendMessage(ChatColor.GRAY+"You redeemed "+(ChatColor.GOLD)+key.getTokens()+(ChatColor.GRAY)+" Tokens");
                plugin.handler.addTokens(player, key.getTokens());
                key.setPlayerCooldown(player, System.currentTimeMillis());
            }else{
                player.sendMessage(ChatColor.RED.toString() + (TimeUnit.MINUTES.toSeconds(key.getCooldown())-TimeUnit.MILLISECONDS.toSeconds(timeLeft)) + " seconds before you can use this feature again.");
            }
        }else{
            player.sendMessage(ChatColor.RED+"Key "+keyName+" is NOT a valid key!");
        }
    }
}