package net.thirdshift.tokens.messages.messageComponents;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TargetMessageComponent extends MessageComponent{
    private String playerName;

    private TargetMessageComponent() {
        super("%target%");
    }
    public TargetMessageComponent(Player player) {
        this();
        playerName=player.getName();
    }

    public TargetMessageComponent(CommandSender sender) {
        this();
        playerName=sender.getName();
    }

    public TargetMessageComponent(String nameIn) {
        this();
        playerName=nameIn;
    }

    @Override
    public String apply(String message) {
        return message.replaceAll(replaces, playerName);
    }
}
