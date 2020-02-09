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
    public Map<String, Object> cooldowns;

    public Key(String keyString){
        this.keyString=keyString;
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
        if(!this.oneTime) {
            if (time < 1) {
                cooldowns.remove(player.getUniqueId().toString());
            } else {
                cooldowns.put(player.getUniqueId().toString(), Long.toString(time));
            }
        }else{
            cooldowns.put(player.getUniqueId().toString(), Long.toString(time));
        }
    }

    public long getPlayerCooldown(Player player){
        if(cooldowns.containsKey(player.getUniqueId().toString())){
            return Long.parseLong((String) cooldowns.get(player.getUniqueId().toString()));
        }
        return 0L;
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
