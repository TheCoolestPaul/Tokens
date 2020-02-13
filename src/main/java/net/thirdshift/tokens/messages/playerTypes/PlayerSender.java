package net.thirdshift.tokens.messages.playerTypes;

import org.bukkit.entity.Player;

public class PlayerSender{

    public String player;

    public PlayerSender(Player player){
        this.player=player.getDisplayName();
    }

    public PlayerSender(String name){ this.player=name; }

}
