package net.thirdshift.tokens.messages.messageData;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerTarget{

    public String player;

    public PlayerTarget(Player player){
        this.player=player.getDisplayName();
    }

    public PlayerTarget(CommandSender sender){ this.player=sender.getName(); }

    public PlayerTarget(String name){ this.player=name; }

}
