package net.thirdshift.tokens;

import net.thirdshift.tokens.util.TokensConfigHandler;
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
    private final TokensConfigHandler configHandler;
    //private Map<UUID, Integer> playerBalances;

    public TokensHandler(final Tokens instance){
        this.plugin=instance;
        configHandler = instance.getTokensConfigHandler();
        //playerBalances = new HashMap<>();
    }

    /**
     * Adds Tokens to a current player's balance
     * @param player Target player
     * @param tokensIn Amount of tokens
     */
    public void addTokens(Player player, int tokensIn){
        if(configHandler.isRunningMySQL()){
            plugin.getMySQL().addTokens(player, tokensIn);
        } else {
            plugin.getSqllite().addTokens(player, tokensIn );
        }
    }

    /**
     * Check a player's balance of tokens.
     * @param player Target player
     * @return Current balance of Player's tokens
     */
    public int getTokens(Player player){
        if(configHandler.isRunningMySQL()){
            return plugin.getMySQL().getTokens(player);
        }else {
            return plugin.getSqllite().getTokens(player);
        }
    }

    /**
     * Sets a player's balance of tokens
     * @param player Target player
     * @param tokensIn Tokens to set
     */
    public void setTokens(Player player, int tokensIn){
        if(configHandler.isRunningMySQL()){
            plugin.getMySQL().setTokens(player, tokensIn);
        }else {
            plugin.getSqllite().setTokens(player, tokensIn);
        }
    }

    /**
     * Removes tokens from a player's balance
     * @param player Target player
     * @param tokensIn Tokens to remove
     */
    public void removeTokens(Player player, int tokensIn){
        if(configHandler.isRunningMySQL()){
            plugin.getMySQL().removeTokens(player, tokensIn);
        }else {
            plugin.getSqllite().setTokens( player, (plugin.getSqllite().getTokens(player) - tokensIn) );
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
