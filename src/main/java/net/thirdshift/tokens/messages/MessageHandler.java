package net.thirdshift.tokens.messages;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.messages.messageComponents.MessageComponent;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
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
     * @param path Path of the message in messages.yml
     * @param components Components used in the message
     * @return The messaged formatted
     */
    public String useMessage(String path, List<MessageComponent> components){
    	String results = messageList.get(path).getColorFormattedString();
    	if ( messageList.containsKey( path ) ) {
            for (MessageComponent component: components) {
                results = component.apply(results);
            }
    	}
        return results == null ? "" : results;
    }

    /**
     * <p>This uses the String.format() to apply parameters to the given message.
     * </p>
     * 
     * @param path Path of the message in messages.yml
     * @param args Arguments to be applied to the String.format(). Does not support the placeholders.
     * @return The string but formatted
     */
    public String formatMessage(String path, Object... args) {
    	String formatted;
    	if ( messageList.containsKey(path) ) {
    		String message = messageList.get(path).getColorFormattedString();
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
        HashMap<String, String> paths = new HashMap<>();
        //FORMAT: path, default value
        paths.put("tokens.main", "&7You have &6%tokens% &7Tokens");
        paths.put("tokens.others", "&e%target% &7has &6%tokens% &7Tokens");
        paths.put("tokens.deny-console", "&7You cannot run this command from console.");

        paths.put("tokens.add.sender", "&7You added &6%tokens% &7Tokens to %target%&7's Tokens.");
        paths.put("tokens.add.receiver", "none");

        paths.put("tokens.give.sender", "&7You sent &6%tokens% &7Tokens to &e%target%");
        paths.put("tokens.give.receiver", "&e%sender%&7 sent &6%tokens% &7Tokens to you");

        paths.put("tokens.remove.sender", "&7You removed &5%tokens% &7Tokens from &e%target%");
        paths.put("tokens.remove.receiver", "none");

        paths.put("tokens.set.sender", "&7You set &e%target% &7Tokens to &5%tokens%");
        paths.put("tokens.set.receiver", "none");

        paths.put("tokens.errors.no-player", "&4Couldn't find player &7%target%&4. Did you spell their username correctly?");
        paths.put("tokens.errors.invalid-command.message", "&4Invalid command use.");
        paths.put("tokens.errors.invalid-command.correction", "&7Command usage: %command_usage%");

        // Token Cache messages:
        paths.put("tokens.cache.menu.help-help", "&7Displays this helpful cache text");
        paths.put("tokens.cache.menu.help-sync", "&7Synchronizes the cache with the database. Purge non-active players.");
        paths.put("tokens.cache.menu.help-stats", "&7Token Cache stats - Displays collected stats");
        paths.put("tokens.cache.menu.help-stats-toggle", "&7Token Cache stats - Toggle on and off");
        paths.put("tokens.cache.menu.help-stats-clear", "&7Token Cache Stats - Clear & reset the stats. If off, it will not start the stats.");
        paths.put("tokens.cache.menu.help-stats-dump", "&7Token Cache Stats Dump - Dumps the player cache and shows internal stats.");
        paths.put("tokens.cache.menu.help-stats-journaling", "&7Token Cache Stats Journaling - Toggle on and off. Player name is optional.");
        paths.put("tokens.cache.sync.start.message", "&7Starting Token Cache database synchronization...");
        paths.put("tokens.cache.sync.completed.message", "&7Finished synchronization: %d players in cache. Unloaded %d players. %d players synchronized. %d players were saved to DB.");
        paths.put("tokens.cache.stats.toggled", "&7Token Cache stats toggled. enabled= &5%b");
        paths.put("tokens.cache.stats.cleared", "&7Token Cache stats have been cleared & reset.");
        paths.put("tokens.cache.stats.journaling-enbled", "&Token Cache Journaling has been enabled.");
        paths.put("tokens.cache.stats.journaling-player", "&Token Cache Journaling is activated on player: %s.");
        paths.put("tokens.cache.stats.journaling-disabled", "&Token Cache Journaling has been disabled.");

        // Start redeem
        paths.put("redeem.mcmmo.redeemed", "&7You redeemed &6%tokens% &7Tokens for the skill &8%skill_name%");
        paths.put("redeem.mcmmo.invalid-skill", "&4Invalid skill name. &7Available skills are: %skill_list%");
        paths.put("redeem.factions", "&7You redeemed &6%tokens% &7Tokens for faction power.");
        paths.put("redeem.vault.sell", "&7You redeemed &6%tokens% &7Tokens for &a$%money%");
        paths.put("redeem.vault.buy", "&7You bought &6%tokens%&7 Tokens for &a$%money%");
        paths.put("redeem.deny.no-money", "&4You don't have enough money for that!");
        paths.put("redeem.deny.no-tokens", "&4You don't have enough Tokens for that!");

        paths.put("combatlogx.deny", "&4You can't use Tokens while in combat! %seconds% Seconds left!");

        for (String pathName : paths.keySet()){
            String pathVal=tryPath(pathName, paths.get(pathName));
            messageList.put(pathName, new Message(pathVal));
        }
        plugin.getLogger().fine("Loaded "+messageList.size() +" messages");
        try {
            messageConfig.save(plugin.getMessageFile());
        } catch (IOException e) {
            plugin.getLogger().warning("Error updating messages.yml");
        }
    }

    public String tryPath(String path, String defaultMessage){
        if( messageConfig.getString(path) != null ){
            plugin.getLogger().fine("Loaded "+path+" "+messageConfig.getString(path));
            String message = messageConfig.getString(path);
            assert message != null;
            if (!message.equalsIgnoreCase("none"))
                return messageConfig.getString(path);
            return "";
        }else {
            messageConfig.set(path, defaultMessage);
            plugin.getLogger().fine("We added a new path! "+path+" With message "+defaultMessage);
            return defaultMessage;
        }
    }

}
