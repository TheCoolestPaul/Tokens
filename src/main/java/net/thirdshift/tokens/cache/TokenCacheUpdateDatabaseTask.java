package net.thirdshift.tokens.cache;

public class TokenCacheUpdateDatabaseTask
		implements Runnable
{
	private TokenCachePlayerData playerData;
	
	public TokenCacheUpdateDatabaseTask( TokenCachePlayerData playerData ) {
		super();
		
		this.playerData = playerData;
	}
	
	public void run() {

		TokenCache tCache = TokenCache.getInstance();
		
		int tokens = playerData.databaseStageTokens();
		
		tCache.getCacheDatabase().addTokens( playerData.getPlayer(), tokens );
		
		playerData.databaseFinalizeTokens();
		
		// If valueUncommitted is non-zero, then that means more
		// transactions have been added and needs to schedule 
		// another database update.  If the job is submitted when 
		// more data is added, it will not submit a new job.
		tCache.submitAsyncDatabaseUpdate( playerData );
		
		// Remove the saved task:
		tCache.getTasks().remove( playerData.getBukkitTask() );
	}

}
