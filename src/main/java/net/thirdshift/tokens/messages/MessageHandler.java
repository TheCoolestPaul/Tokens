package net.thirdshift.tokens.messages;

import net.thirdshift.tokens.Tokens;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandler {

    private Tokens plugin;
    private FileConfiguration messageConfig;

    public Map<String, Message> messageList = null;

    public MessageHandler(Tokens instance){
        this.plugin=instance;
    }

    public Message getMessage(String path){ return messageList.get(path); }

    public String useMessage(String path, List<Object> objectList){
        return messageList.get(path).use(objectList);
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
            messageList.put(pathName, new Message(pathVal));
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
