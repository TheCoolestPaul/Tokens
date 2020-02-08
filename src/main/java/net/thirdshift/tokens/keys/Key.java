package net.thirdshift.tokens.keys;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Key {

    public String keyString;
    public boolean enabled;
    public boolean oneTime;
    public int tokens;
    public long cooldown; // in minutes
    public Map<Player, Long> cooldowns;

    public Key(String keyString){
        this.keyString=keyString;
        this.cooldowns=new HashMap<>();
    }

    public Key(String keyString, boolean enabled, boolean oneTime, int tokens, long cooldown){
        this.keyString=keyString;
        this.enabled=enabled;
        this.oneTime=oneTime;
        this.tokens=tokens;
        this.cooldown=cooldown;
        this.cooldowns=new HashMap<>();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public int getTokens() {
        return tokens;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setPlayerCooldown(Player player, long time){
        if(time < 1){
            cooldowns.remove(player);
        }else{
            cooldowns.put(player, time);
        }
    }

    public long getPlayerCooldown(Player player){
        return cooldowns.getOrDefault(player, (long) 0);
    }

    @Override
    public String toString() {
        return "Key{" +
                "keyString='" + keyString + '\'' +
                ", enabled=" + enabled +
                ", oneTime=" + oneTime +
                ", tokens=" + tokens +
                ", cooldown=" + cooldown +
                '}';
    }
}
