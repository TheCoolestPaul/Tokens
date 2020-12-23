package net.thirdshift.tokens.messages;

import net.thirdshift.tokens.Tokens;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandler {

    private final Tokens plugin;
    private FileConfiguration messageConfig;

    public Map<String, Message> messageList = null;

    public MessageHandler(Tokens instance){
        this.plugin=instance;
    }

    public Message getMessage(String path){ return messageList.get(path); }

    /**
     * <p>Uses the give message, but if the path does not exist, then it returns
     * an empty String so it does not generate a NullPointerException.
     * </p>
     * 
     * @param path
     * @param objectList
     * @return
     */
    public String useMessage(String path, List<Object> objectList){
    	String results = null;
    	if ( messageList.containsKey( path ) ) {
    		results = messageList.get(path).use(objectList);
    	}
        return results == null ? "" : results;
    }

    /**
     * <p>This uses the String.format() to apply parameters to the given message.
     * </p>
     * 
     * @param path
     * @param args Arguments to be applied to the String.format(). Does not support the placeholders.
     * @return
     */
    public String formatMessage(String path, Object... args) {
    	String formatted = null;
    	if ( messageList.containsKey(path) ) {
    		String message = messageList.get(path).getFormatted();
    		formatted = String.format( message, args );
    	}
    	else {
    		formatted = "Tokens message failure: Could not find message path: " + path + 
					" Please report to an admin and have them check token's message file.";
    	}
    	return formatted;
    }
    
    public void loadMessages(){
        this.messageConfig=plugin.getMessageConfig();
        if(messageList!=null){
            messageList.clear();
        }
        messageList = new HashMap<>();
        List<String> paths = new ArrayList<>();
        paths.add("tokens.main");
        paths.add("tokens.others");
        paths.add("tokens.console");

        paths.add("tokens.add.sender");
        paths.add("tokens.add.receiver");

        paths.add("tokens.give.sender");
        paths.add("tokens.give.receiver");

        paths.add("tokens.remove.sender");
        paths.add("tokens.remove.receiver");

        paths.add("tokens.set.sender");
        paths.add("tokens.set.receiver");

        paths.add("tokens.errors.no-player");
        paths.add("tokens.errors.invalid-command.message");
        paths.add("tokens.errors.invalid-command.correction");

        
        // Token Cache messages:
        paths.add("tokens.cache.menu.help-help");
        paths.add("tokens.cache.menu.help-sync");
        paths.add("tokens.cache.menu.help-stats");
        paths.add("tokens.cache.sync.start.message");
        paths.add("tokens.cache.sync.completed.message");
        
        
        // Start redeem
        paths.add("redeem.mcmmo.redeemed");
        paths.add("redeem.mcmmo.invalid-skill");

        paths.add("redeem.factions");

        paths.add("redeem.vault.sell");
        paths.add("redeem.vault.buy");

        paths.add("redeem.errors.no-money");
        paths.add("redeem.errors.not-enough");

        paths.add("combatlogx.deny");

        for (String pathName : paths){
            String pathVal=tryPath(pathName);
            messageList.put(pathName, new Message(pathVal, plugin));
        }
    }

    public String tryPath(String path){
        if(messageConfig.contains(path)){
            return messageConfig.getString(path);
        }else {
            return "";
        }
    }

}
