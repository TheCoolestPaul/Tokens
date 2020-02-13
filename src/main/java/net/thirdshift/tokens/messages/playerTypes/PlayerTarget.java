package net.thirdshift.tokens.messages.playerTypes;

import org.bukkit.entity.Player;

public class PlayerTarget{

    public String player;

    public PlayerTarget(Player player){
        this.player=player.getDisplayName();
    }

    public PlayerTarget(String name){ this.player=name; }

}
