package net.thirdshift.tokens.cache;

public class TokenCacheLoadPlayerTask
implements Runnable
{
	private TokenCachePlayerData playerData;

	public TokenCacheLoadPlayerTask( TokenCachePlayerData playerData ) {
		super();
	
		this.playerData = playerData;
	}

	public void run() {
	
		TokenCache tCache = TokenCache.getInstance();
		
		
		int tokens = tCache.getCacheDatabase().getTokens( playerData.getPlayer() );
		
		String stats = playerData.setInitialValue( tokens );
		tCache.getPlugin().getLogger().info( stats );
		
		// If valueUncommitted is non-zero, then that means more
		// transactions have been added and needs to schedule 
		// another database update.  If the job is submitted when 
		// more data is added, it will not submit a new job.
		tCache.submitAsyncDatabaseUpdate( playerData );
		
		// Remove the saved task:
		tCache.getTasks().remove( playerData.getBukkitTask() );
	}

}
