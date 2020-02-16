package net.thirdshift.tokens.messages.messageData;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerSender{

    public String player;

    public PlayerSender(Player player){
        this.player=player.getDisplayName();
    }

    public PlayerSender(CommandSender sender){ this.player=sender.getName(); }

    public PlayerSender(String name){ this.player=name; }

}
