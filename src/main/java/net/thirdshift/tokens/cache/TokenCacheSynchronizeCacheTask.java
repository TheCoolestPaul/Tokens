package net.thirdshift.tokens.cache;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * <p>This task performs a few different housekeeping operations
 * that are focused on updating the status of the players that are 
 * in the cache.
 * </p>
 * 
 * <p>The first major function this performs, is to check to see if
 * the player is still online. The cache should only contain online
 * players, and so this will purge offline players. It should be
 * noted that there should never be a situation where a player 
 * would be found in this state, since they should be unloaded at
 * the time when they leave the game. But just in case this will 
 * provide a cache clean up.
 * </p>
 * 
 * <p>Another important function this performs is it "tries" to 
 * update the Player instance for the player.  If a player leaves
 * the game and they are not unloaded, then their new Player 
 * object will not be in the cache.  Therefore if the Player
 * object stored in the cache is not the exact same object that
 * is returned by the server, then this will update the cache with
 * the most current Player object for that player.
 * </p>
 * 
 * <p>Then, finally, the task that is the primary purpose of this whole
 * process, is to check all active players to ensure the cache's 
 * database amount matches what is actually within the database.  If
 * they don't match, then update the cache to reflect the database's value.
 * This is check is performed in such a way that if a player is undergoing
 * a database update, then it will be able to take in to consideration the 
 * projected final database value and this will still be able to make the
 * correct adjustments.
 * </p>
 *
 */
public class TokenCacheSynchronizeCacheTask
				implements Runnable
{
	private CommandSender commandSender;
	
	public TokenCacheSynchronizeCacheTask( CommandSender commandSender ) {
		super();
		
		this.commandSender = commandSender;
	}
	
	public void run() {

		int cachedPlayers = 0;
		int cachedUnloaded = 0;
//		int playersUpdated = 0;
		int playersSyncd = 0;
		
		TokenCache tCache = TokenCache.getInstance();
		
		List<String> keys = new ArrayList<>( tCache.getPlayerStrings().keySet() );
		
		for ( String key : keys ) {
			TokenCachePlayerData playerData = tCache.getPlayerStrings().get( key );
			
			// Check to make sure the player is still online, if not, remove them from the cache:
			Player currentPlayer = tCache.getPlugin().getServer().getPlayer( playerData.getPlayer().getUniqueId() );
			
			if ( currentPlayer == null || !currentPlayer.isOnline() ) {
				// Player is no longer online so unload them:
				tCache.submitAsyncUnloadPlayer( playerData.getPlayer() );
				cachedUnloaded++;
			}
			else {
				// NOTE: currentPlayer.equals() does not work correctly... skip this for now:
//				if ( currentPlayer.equals( playerData.getPlayer() )  ) {
//					// If a player logs off and then back on, will the Player object be the same?
//					// Not sure how you could tell, but I suspect they will be different objects.
//					playerData.setPlayer( currentPlayer );
//					playersUpdated++;
//				}
				
				// check the synchronization:
				int tokens = tCache.getCacheDatabase().getTokens( playerData.getPlayer() );
				
				if ( playerData.synchronizeFromDatabase( tokens ) ) {
					playersSyncd++;
				}
			}
				
			cachedPlayers++;
		}
		
		
		String message = tCache.getPlugin().messageHandler.
        		formatMessage( "tokens.cache.sync.completed.message", 
        				cachedPlayers, cachedUnloaded, playersSyncd );
		
		if ( commandSender != null ) {
			commandSender.sendMessage( message );
		}
		else {
			tCache.getPlugin().getLogger().info( message );
		}

		
//		List<TokenCachePlayersTokensData> players = 
//								tCache.getCacheDatabase().getAllPlayerTokens();
//		
//		for ( TokenCachePlayersTokensData playerTokens : players ) {
//			
//			if ( tCache.getPlayerStrings().containsKey( playerTokens.getUuid() ) ) {
//				
//				TokenCachePlayerData playerData = tCache.getPlayerStrings().get( playerTokens.getUuid() );
//				
//				playerData.synchronizeFromDatabase( playerTokens.getTokens() );
//			}
//			
//		}

		
	}
}
