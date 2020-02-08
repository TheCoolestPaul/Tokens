package net.thirdshift.tokens.commands.redeem;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.keys.Key;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class key {
    public static void redeemKey(Player player, String keyName, Tokens plugin){
        if(plugin.keyHander.isValidKey(keyName)){
            if(plugin.keyHander.getKey(keyName).enabled) {
                Key key = plugin.keyHander.getKey(keyName);
                if(key.getPlayerCooldown(player) != -1) {
                    long timeLeft = System.currentTimeMillis() - key.getPlayerCooldown(player);
                    if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= TimeUnit.MINUTES.toSeconds(key.getCooldown())) {
                        player.sendMessage(ChatColor.GRAY + "You redeemed " + (ChatColor.GOLD) + key.getTokens() + (ChatColor.GRAY) + " Tokens");
                        plugin.handler.addTokens(player, key.getTokens());
                        if (key.oneTime) {
                            key.setPlayerCooldown(player, -1);
                        } else {
                            key.setPlayerCooldown(player, System.currentTimeMillis());
                        }
                    } else {
                        long actTimeLeft = (TimeUnit.MINUTES.toMillis(key.getCooldown()) - timeLeft);
                        if(actTimeLeft >= 1000 && TimeUnit.MILLISECONDS.toSeconds(actTimeLeft) < 60){
                            player.sendMessage(ChatColor.GRAY.toString()+TimeUnit.MILLISECONDS.toSeconds(actTimeLeft)+" seconds before you can redeem key "+ChatColor.RED+key.keyString+ChatColor.GRAY+" again.");
                        } else if (TimeUnit.MILLISECONDS.toSeconds(actTimeLeft) >= 60 && TimeUnit.MILLISECONDS.toMinutes(actTimeLeft) < 60){
                            player.sendMessage(ChatColor.GRAY.toString()+TimeUnit.MILLISECONDS.toMinutes(actTimeLeft)+" minutes before you can redeem key "+ChatColor.RED+key.keyString+ChatColor.GRAY+" again.");
                        }else if (TimeUnit.MILLISECONDS.toMinutes(actTimeLeft) >= 60 && TimeUnit.MILLISECONDS.toHours(actTimeLeft) < 24){
                            player.sendMessage(ChatColor.GRAY.toString()+TimeUnit.MILLISECONDS.toHours(actTimeLeft)+" hours before you can redeem key "+ChatColor.RED+key.keyString+ChatColor.GRAY+" again");
                        }else if(TimeUnit.MILLISECONDS.toHours(actTimeLeft) >= 24 && TimeUnit.MILLISECONDS.toDays(actTimeLeft) < 365){
                            player.sendMessage(ChatColor.GRAY.toString()+TimeUnit.MILLISECONDS.toDays(actTimeLeft)+" days before you can redeem key "+ChatColor.RED+key.keyString+ChatColor.GRAY+" again");
                        }
                    }
                }else{
                    player.sendMessage(ChatColor.GRAY+"You can't redeem key "+key.keyString+" again.");
                }
            }else{
                player.sendMessage(ChatColor.RED+"Key "+keyName+" is NOT a valid key!");
            }
        }else{
            player.sendMessage(ChatColor.RED+"Key "+keyName+" is NOT a valid key!");
        }
    }
}
