package net.thirdshift.tokens.cache;

import org.bukkit.entity.Player;

import net.thirdshift.tokens.Tokens;

public class TokenCacheDatabase {

	private TokenCache tokenCache;
	
	protected TokenCacheDatabase( TokenCache tokenCache ) {
		super();
		
		this.tokenCache = tokenCache;
	}
	
    private TokenCache getTokenCache() {
		return tokenCache;
	}

    private Tokens getPlugin() {
    	return getTokenCache().getPlugin();
    }
    
	/**
     * Check a player's balance of tokens.
     * @param player Target player
     * @return Current balance of Player's tokens
     */
    public int getTokens(Player player){
        if( getPlugin().getTokensConfigHandler().isRunningMySQL() ) {
            return getPlugin().getMySQL().getTokens(player);
        }
        else {
            return getPlugin().getSqllite().getTokens(player);
        }
    }

    /**
     * Adds Tokens to a current player's balance
     * @param player Target player
     * @param tokensIn Amount of tokens
     */
    public void addTokens(Player player, int tokensIn){
        if( getPlugin().getTokensConfigHandler().isRunningMySQL() ) {
        	getPlugin().getMySQL().addTokens(player, tokensIn);
        } 
        else {
        	getPlugin().getSqllite().addTokens(player, tokensIn );
        }
    }
    
    
    /**
     * Sets a current player's balance to a specific amount of tokens.
     * 
     * @param player Target player
     * @param tokensIn Amount of tokens
     */
    public void setTokens(Player player, int tokensIn){
    	if( getPlugin().getTokensConfigHandler().isRunningMySQL() ) {
    		getPlugin().getMySQL().setTokens(player, tokensIn);
    	} 
    	else {
    		getPlugin().getSqllite().setTokens(player, tokensIn );
    	}
    }

	public void closeConnections()
	{
    	if( getPlugin().getTokensConfigHandler().isRunningMySQL() ) {
    		getPlugin().getMySQL().closeConnection();
    	} 
    	else {
    		getPlugin().getSqllite().closeConnection();
    	}
		
	}
    
}
