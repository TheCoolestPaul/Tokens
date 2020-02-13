package net.thirdshift.tokens.messages;

import net.thirdshift.tokens.Tokens;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandler {

    private Tokens plugin;

    public Map<String, Message> messageList = null;

    public MessageHandler(Tokens instance){
        this.plugin=instance;
    }

    public Message getMessage(String path){ return messageList.get(path); }

    public String useMessage(String path, List<Object> objectList){
        return messageList.get(path).use(objectList);
    }

    public void loadMessages(){
        if(messageList!=null){
            messageList.clear();
        }
        messageList = new HashMap<>();
        FileConfiguration messageConfig = plugin.getMessageConfig();
        messageList.put("tokens.main", new Message(messageConfig.getString("tokens.main")));
        messageList.put("tokens.give.sender", new Message(messageConfig.getString("tokens.give.sender")));
        messageList.put("tokens.give.receiver", new Message(messageConfig.getString("tokens.give.receiver")));
        messageList.put("redeem.mcmmo", new Message(messageConfig.getString("redeem.mcmmo")));
        messageList.put("redeem.factions", new Message(messageConfig.getString("redeem.factions")));
        messageList.put("redeem.vault.sell", new Message(messageConfig.getString("redeem.vault.sell")));
        messageList.put("combatlogx.deny", new Message(messageConfig.getString("combatlogx.deny")));
    }
}
