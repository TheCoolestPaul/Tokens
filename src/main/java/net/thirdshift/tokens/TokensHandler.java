package net.thirdshift.tokens;

import org.bukkit.entity.Player;

//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;

/*
TODO: Make this more efficient
via HashMap, limit read/writes from
MySQL or SQLLite.
 */

public class TokensHandler {

    private final Tokens plugin;
    //private Map<UUID, Integer> playerBalances;

    public TokensHandler(final Tokens instance){
        this.plugin=instance;
        //playerBalances = new HashMap<>();
    }

    /**
     * Adds Tokens to a current player's balance
     * @param player Target player
     * @param tokensIn Amount of tokens
     */
    public void addTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().addTokens(player, tokensIn);
        }else if (plugin.sqlliteEnabled){
            plugin.getSqllite().setTokens(player, (plugin.getSqllite().getTokens(player) + tokensIn) );
        }else {
            plugin.getLogger().severe("MySQL isn't configured properly!");
        }
    }

    /**
     * Check a player's balance of tokens.
     * @param player Target player
     * @return Current balance of Player's tokens
     */
    public int getTokens(Player player){
        if(plugin.mysqlEnabled){
            return plugin.getMySQL().getTokens(player);
        }else if (plugin.sqlliteEnabled){
            return plugin.getSqllite().getTokens(player);
        }
        plugin.getLogger().severe("MySQL isn't configured properly!");
        return 0;
    }

    /**
     * Sets a player's balance of tokens
     * @param player Target player
     * @param tokensIn Tokens to set
     */
    public void setTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().setTokens(player, tokensIn);
        }else if (plugin.sqlliteEnabled){
            plugin.getSqllite().setTokens(player, tokensIn);
        }else {
            plugin.getLogger().severe("MySQL isn't configured properly!");
        }
    }

    /**
     * Removes tokens from a player's balance
     * @param player Target player
     * @param tokensIn Tokens to remove
     */
    public void removeTokens(Player player, int tokensIn){
        if(plugin.mysqlEnabled){
            plugin.getMySQL().removeTokens(player, tokensIn);
        }else if (plugin.sqlliteEnabled){
            plugin.getSqllite().setTokens( player, (plugin.getSqllite().getTokens(player) - tokensIn) );
        }else {
            plugin.getLogger().severe("MySQL isn't configured properly!");
        }
    }

    /**
     * Used to see if a player has enough Tokens
     * @param player Target player
     * @param tokensIn Tokens to check for
     * @return True if player has tokens, false player does not have enough tokens
     */
    public boolean hasTokens(Player player, int tokensIn){
        return getTokens(player) >= tokensIn;
    }

}
