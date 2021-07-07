package net.thirdshift.tokens.messages.messageComponents;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SenderMessageComponent extends MessageComponent{
    private String playerName;
    private SenderMessageComponent() {
        super("%sender%");
    }

    public SenderMessageComponent(Player player){
        this();
        playerName=player.getName();
    }

    public SenderMessageComponent(CommandSender sender){
        this();
        playerName=sender.getName();
    }

    @Override
    public String apply(String message) {
        return message.replaceAll(replaces, playerName);
    }
}
