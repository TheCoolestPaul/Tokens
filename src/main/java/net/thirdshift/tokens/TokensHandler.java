package net.thirdshift.tokens;

import org.bukkit.entity.Player;

public class TokensHandler {

    private Tokens plugin;
    public TokensHandler(Tokens instance){this.plugin=instance;}

    public void addTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().addTokens(player, tokensIn);
        }else if (plugin.sqlliteEnabled){
            plugin.getSqllite().setTokens(player, (plugin.getSqllite().getTokens(player) + tokensIn) );
        }else {
            plugin.getLogger().severe("MySQL isn't configured properly!");
        }
    }

    public int getTokens(Player player){
        if(plugin.mysqlEnabled){
            return plugin.getMySQL().getTokens(player);
        }else if (plugin.sqlliteEnabled){
            return plugin.getSqllite().getTokens(player);
        }
        plugin.getLogger().severe("MySQL isn't configured properly!");
        return 0;
    }

    public void setTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().setTokens(player, tokensIn);
        }else if (plugin.sqlliteEnabled){
            plugin.getSqllite().setTokens(player, tokensIn);
        }else {
            plugin.getLogger().severe("MySQL isn't configured properly!");
        }
    }

    public void removeTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().removeTokens(player, tokensIn);
        }else if (plugin.sqlliteEnabled){
            plugin.getSqllite().setTokens( player, (plugin.getSqllite().getTokens(player) - tokensIn) );
        }else {
            plugin.getLogger().severe("MySQL isn't configured properly!");
        }
    }

}
