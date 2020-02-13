package net.thirdshift.tokens.keys;

import net.thirdshift.tokens.Tokens;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyHandler {
    public Map<String, Key> keys;
    private Tokens plugin;
    private FileConfiguration keyConfig;
    private File keyData;

    public KeyHandler(Tokens instance){
        this.plugin=instance;
        this.keyConfig=plugin.getKeyConfig();
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

        File storageFolder = new File(plugin.getDataFolder(), "Storage");
        keyData = new File(storageFolder, "KeyData");

        if (!storageFolder.exists()){
            storageFolder.mkdirs();
            plugin.getLogger().info("Made /Tokens/Storage/");
        }

        if (!keyData.exists()){
            keyData.mkdirs();
            plugin.getLogger().info("Made /Tokens/Storage/KeyData/");
        }

        plugin.getLogger().info("Loading keys");
        List<String> keyStrings = keyConfig.getStringList("keys");
        for(String s : keyStrings){
            Key key = new Key(s);

            boolean enabled = ((keyConfig.contains(s + ".enabled")) && keyConfig.getBoolean(s + ".enabled"));
            key.setEnabled(enabled);

            boolean oneTime = ((keyConfig.contains(s + ".one-time")) && keyConfig.getBoolean(s + ".one-time"));
            key.setOneTime(oneTime);

            int tokens = ( (keyConfig.contains(s + ".tokens")) ? keyConfig.getInt(s+".tokens") : 1 );
            key.setTokens(tokens);

            long cooldown = (keyConfig.contains(s + ".cooldown")) ? keyConfig.getLong(s+".cooldown") : 120L;
            key.setCooldown(cooldown);

            keys.put(s,key);
            plugin.getLogger().info("Added "+key);
        }
        plugin.getLogger().info("Finished adding "+keyStrings.size()+" keys");

        keysFromYAML(keyData);

    }

    public void saveKeyCooldown(){
        plugin.getLogger().info("Saving KeyData");
        keys.forEach((k,v) -> keysToYAML(keyData, v));
    }

    public void keysToYAML(File storage, Key key){
        for (Map.Entry<String, Key> entry : keys.entrySet()) {
            String k = entry.getKey();
            Key v = entry.getValue();

            File file = new File(storage, k + ".yml");
            YamlConfiguration fileconfig = YamlConfiguration.loadConfiguration(file);
            fileconfig.createSection("cooldowns", v.cooldowns);
            try {
                fileconfig.save(file);
            }catch(IOException ex){
                plugin.getLogger().severe("Problem saving KeyData for key "+v.toString());
                plugin.getLogger().severe(ex.toString());
            }
        }
    }

    public void keysFromYAML(File storage){// Needs to run AFTER initKeys()
        plugin.getLogger().info("Starting to load KeyData");
        File[] files = storage.listFiles();
        for(String s : keys.keySet()){
            if(files!=null) {
                for (File file : files) {
                    String keyName = file.getName().substring(0,file.getName().indexOf('.'));
                    if(isValidKey(keyName)) {
                        YamlConfiguration fileconfig = YamlConfiguration.loadConfiguration(file);
                        keys.get(keyName).cooldowns= fileconfig.getConfigurationSection("cooldowns").getValues(false);
                    }else{
                        if(file.exists()){
                            boolean hapened = file.delete();
                            if(hapened){
                                plugin.getLogger().info("We removed an old key data file");
                            }else{
                                plugin.getLogger().info("We didn't remove an old key data file?");
                            }
                        }
                    }
                }
            }else{
                plugin.getLogger().info("No KeyData to load");
            }
        }
        plugin.getLogger().info("Finished loading KeyData");
    }
}
