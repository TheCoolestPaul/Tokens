package net.thirdshift.tokens.keys;

import net.thirdshift.tokens.Tokens;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class KeyHandler {
    public ArrayList<Key> keys;
    private Tokens plugin;
    private FileConfiguration keyConfig;

    public KeyHandler(Tokens instance){
        this.plugin=instance;
        this.keyConfig=plugin.getCustomConfig();
        this.keys=new ArrayList<>();
        initKeys();
    }

    public List<Key> getKeys() {
        return keys;
    }

    public Key getKey(String keyString){
        for(Key key : keys){
            if(key.keyString.equals(keyString)){
                return key;
            }
        }
        return null;
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

            double cooldown = ( (keyConfig.contains(s + ".cooldown")) ? keyConfig.getDouble(s+".cooldown") : 100.0 );
            key.setCooldown(cooldown);

            keys.add(key);
            plugin.getLogger().info("Added "+key);
        }
        plugin.getLogger().info("Finished initing "+keyStrings.size()+" keys");
    }

}
