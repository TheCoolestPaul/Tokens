package net.thirdshift.tokens.keys;

import net.thirdshift.tokens.Tokens;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyHandler {
    public Map<String, Key> keys;
    private Tokens plugin;
    private FileConfiguration keyConfig;

    public KeyHandler(Tokens instance){
        this.plugin=instance;
        this.keyConfig=plugin.getCustomConfig();
        this.keys=new HashMap<>();
        initKeys();
    }

    public Map<String, Key> getKeys() {
        return keys;
    }

    public Key getKey(String keyString){
        return keys.get(keyString);
    }

    public boolean isValidKey(String keyString){
        return keys.get(keyString) != null;
    }

    public void initKeys(){
        List<String> keyStrings = keyConfig.getStringList("keys");
        for(String s : keyStrings){
            Key key = new Key(s);

            boolean enabled = ((keyConfig.contains(s + ".enabled")) && keyConfig.getBoolean(s + ".enabled"));
            key.setEnabled(enabled);

            boolean oneTime = ((keyConfig.contains(s + ".one-time")) && keyConfig.getBoolean(s + ".one-time"));
            key.setOneTime(oneTime);

            int tokens = ( (keyConfig.contains(s + ".tokens")) ? keyConfig.getInt(s+".tokens") : 1 );
            key.setTokens(tokens);

            long cooldown = (keyConfig.contains(s + ".cooldown")) ? keyConfig.getLong(s+".cooldown") : (long) 120;
            key.setCooldown(cooldown);

            keys.put(s,key);
            plugin.getLogger().info("Added "+key);
        }
        plugin.getLogger().info("Finished initing "+keyStrings.size()+" keys");
    }

}
