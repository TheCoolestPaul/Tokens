package net.thirdshift.tokens.keys;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Key {

    public String keyString;
    public boolean enabled;
    public boolean oneTime;
    public int tokens;
    public double cooldown;
    public Map<Player, Long> cooldowns;

    public Key(String keyString){
        this.keyString=keyString;
        this.cooldowns=new HashMap<>();
    }

    public Key(String keyString, boolean enabled, boolean oneTime, int tokens, double cooldown){
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

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }

    public void setPlayerCooldown(Player player, long time){
        if(time < 1){
            cooldowns.remove(player);
        }else{
            cooldowns.put(player, time);
        }
    }

    public long getCooldown(Player player){
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
