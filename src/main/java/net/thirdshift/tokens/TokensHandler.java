package net.thirdshift.tokens;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TokensHandler {
    private Tokens plugin;
    public TokensHandler(Tokens instance){this.plugin=instance;}

    public void addTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().addTokens(player, tokensIn);
        }else{
            plugin.getSqllite().setTokens(player, (plugin.getSqllite().getTokens(player) + tokensIn) );
        }
    }

    public int getTokens(Player player){
        if(plugin.mysqlEnabled){
            return plugin.getMySQL().getTokens(player);
        }else{
            return plugin.getSqllite().getTokens(player);
        }
    }

    public void setTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().setTokens(player, tokensIn);
        }else{
            plugin.getSqllite().setTokens(player, tokensIn);
        }
    }

    public void removeTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().removeTokens(player, tokensIn);
        }else{
            plugin.getSqllite().setTokens( player, (plugin.getSqllite().getTokens(player) - tokensIn) );
        }
    }

}
