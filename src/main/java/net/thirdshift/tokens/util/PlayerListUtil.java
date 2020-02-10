package net.thirdshift.tokens.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerListUtil {
    public static List<String> playerListUtil(Player sender, boolean showSelf){
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        List<String> playerNames = new ArrayList<>();
        players.forEach((ply)->{
            if(!showSelf) {
                if (!ply.equals(sender))
                    playerNames.add(ply.getName());
            }else{
                playerNames.add(ply.getName());
            }
        });
        return playerNames;
    }
}
