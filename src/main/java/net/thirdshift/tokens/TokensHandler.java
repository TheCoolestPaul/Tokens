package net.thirdshift.tokens;

import org.bukkit.entity.Player;

import net.thirdshift.tokens.cache.TokenCache;

/**
 *
 * @Changes v3.9.9
 * This class acts more like an interface between
 * direct cache contact than actually managing DB connections.
 *
 * For more direct DB communications see
 * [the cache class]{@link net.thirdshift.tokens.cache.TokenCache}
 */
public class TokensHandler {

    /**
     * Adds Tokens to a current player's balance
     * @param player Target player
     * @param tokensIn Amount of tokens
     */
    public void addTokens(Player player, int tokensIn){
    	TokenCache.getInstance().addTokens( player, tokensIn );
    }

    /**
     * Get the Token balance of a player
     * @param player Target player
     * @return Current Token balance of player
     */
    public int getTokens(Player player){
    	return TokenCache.getInstance().getTokens( player );
    }

    /**
     * Sets a player's balance of Tokens
     * @param player Target player
     * @param tokensIn Tokens to set
     */
    public void setTokens(Player player, int tokensIn){
    	TokenCache.getInstance().setTokens( player, tokensIn );
    }

    /**
     * Removes tokens from a player's balance
     * @param player Target player
     * @param tokensIn Tokens to remove
     */
    public void removeTokens(Player player, int tokensIn){
    	TokenCache.getInstance().removeTokens( player, tokensIn );
    }

    /**
     * Check if a player has enough tokens. Mostly used for redeeming things.
     * @param player Target player
     * @param tokensIn Tokens to check for
     * @return True if player has enough tokens, false player does not have enough tokens
     */
    public boolean hasEnoughTokens(Player player, int tokensIn){
    	return TokenCache.getInstance().hasTokens( player, tokensIn );
    }

}
