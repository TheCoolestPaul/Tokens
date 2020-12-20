package net.thirdshift.tokens.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.thirdshift.tokens.Tokens;

public class TokenCache {
	
	public static final String TOKEN_CACHE_IS_ENABLED = "tokencache.is_enabled";
	public static final String TOKEN_CACHE_WRITE_DELAY = "tokencache.write_delay";
	public static final long TOKEN_CACHE_WRITE_DELAY_VALUE_MS = 10000; // 10 seconds
	public static final String TOKEN_CACHE_TIME_TO_LIVE = "tokencache.time_to_live";
	public static final long TOKEN_CACHE_TIME_TO_LIVE_VALUE_MS = 30 * 60 * 1000; // 30 mins
	
	private static TokenCache instance;
	
	private boolean enabled = false;
	private long writeDelay = 0L;
	
	
	private Map<UUID, TokenCachePlayerData> players;
	private TreeMap<String, TokenCachePlayerData> playerStrings;
	
	private Map<BukkitTask, TokenCachePlayerData> tasks;
	
	private TokenCacheDatabase cacheDatabase;

	private Tokens plugin;
	
	private TokenCache() {
		super();
		
		this.players = new HashMap<>();
		this.playerStrings = new TreeMap<>();
		
		this.tasks = new HashMap<>();
		
//		this.usersDirty = new ArrayList<>();

	}
	
	public static TokenCache getInstance() {
		if ( instance == null ) {
			synchronized( TokenCache.class ) {
				if ( instance == null ) {
					instance = new TokenCache();
				}
			}
		}
		return instance;
	}

	public static void initialize( Tokens plugin ) {
		getInstance().internalInititalize( plugin );
	}
	
	private void internalInititalize( Tokens plugin ) {
		
		this.plugin = plugin;
		
		this.cacheDatabase = new TokenCacheDatabase( this );

		
		FileConfiguration config = plugin.getConfig();

		TokenCacheEvents cacheEvents = new TokenCacheEvents();
		plugin.getServer().getPluginManager().registerEvents(cacheEvents, plugin);
		
		boolean dirty = false;
		
		// If cache entries do not exist in the config file, then add them:
		if ( !config.contains( TOKEN_CACHE_IS_ENABLED ) ) {
			config.addDefault( TOKEN_CACHE_IS_ENABLED, false );
			dirty = true;
		}
		
		if ( !config.contains( TOKEN_CACHE_WRITE_DELAY ) ) {
			config.addDefault( TOKEN_CACHE_WRITE_DELAY, TOKEN_CACHE_WRITE_DELAY_VALUE_MS );
			dirty = true;
		}
		
		if ( dirty ) {
			getPlugin().saveConfig();
		}

		// Load settings from config:
		setEnabled( config.isBoolean( TOKEN_CACHE_IS_ENABLED ));
		setWriteDelay( config.getLong( TOKEN_CACHE_WRITE_DELAY ) );

		getPlugin().getLogger().info( "TokenCache: " + 
				TOKEN_CACHE_IS_ENABLED + ": " + isEnabled() + "  " +
				"  " + TOKEN_CACHE_WRITE_DELAY + ": " + getWriteDelay() );
	}
	
	/**
	 * <p>The plugin is shutting down so flush all dirty cache items, but first
	 * disable the caching so as any changes are passed through directly to the
	 * database.
	 * </p>
	 * 
	 * <p>Since the plugin is shutting down, flush the cache synchronously in this
	 * thread.  Do not submit, or the database may be shutdown before all dirty
	 * cache items can be saved.
	 * </p>
	 */
	public static void onDisable() {
		getInstance().onDisableInternal();
		
	}
	
	/**
	 * <p>This shuts down the cache and unloads all the players while 
	 * updating the database if they have uncommitted values.  This also
	 * will cancel any outstanding tasks and then flush the uncommitted
	 * values if they still exist.
	 * </p>
	 * 
	 * <p>This function runs all database transactions synchronously so as to 
	 * ensure all data is writted to the database before the server is 
	 * terminated.
	 * </p>
	 * 
	 */
	private void onDisableInternal() {
		
		// Disable the cache so future hits go straight to the database:
		setEnabled( false );
		
		// save all dirty cache items and purge cache:
		if ( getPlayers().size() > 0 ) {

			// Create a new set so as to prevent keys from being removed when purging
			// the getPlayers() collection:
			Set<UUID> keys = new TreeSet<>(getPlayers().keySet());

			for ( UUID key : keys ) {
				// Remove the player from the cache and get the playerData:
				TokenCachePlayerData playerData = getPlayers().remove( key );
				
				getPlayerStrings().remove( key.toString() );
				
				if ( playerData != null ) {
					
					// If the player has uncommitted value, then flush it to the database:
					if ( playerData.getValueUncommitted() > 0 ) {
						
						int tokens = playerData.databaseStageTokens();
						getCacheDatabase().addTokens( playerData.getPlayer(), tokens );
						playerData.databaseFinalizeTokens();
					}
				}
			}
			
		}
		
		// Cancel and flush any uncompleted tasks that are scheduled to run:
		if ( getTasks().size() > 0  ) {
			List<BukkitTask> keys = new ArrayList<>( getTasks().keySet() );
			
			for ( BukkitTask key : keys ) {
				
				if ( !key.isCancelled() ) {
					// Cancel the task:
					key.cancel();
					
					// Remove the task and get the player data:
					TokenCachePlayerData playerData = getTasks().remove( key );
					
					if ( playerData != null ) {
						
						// If the player has uncommitted value, then flush it to the database:
						if ( playerData.getValueUncommitted() > 0 ) {
							
							int tokens = playerData.databaseStageTokens();
							getCacheDatabase().addTokens( playerData.getPlayer(), tokens );
							playerData.databaseFinalizeTokens();
						}
					}
					
				}
			}
			
		}
		
		// Shutdown the database connections:
		getCacheDatabase().closeConnections();
		
	}

	
	private long getCacheWriteDelay() {
		return getPlugin().getConfig().getLong( TOKEN_CACHE_WRITE_DELAY );
	}
	
	private TokenCachePlayerData getPlayer( Player player ) {
		
		if ( !getPlayers().containsKey( player.getUniqueId() ) ) {
			
			// Load the player's existing balance:
			submitAsyncLoadPlayer( player );
		}
		
		return getPlayers().get( player.getUniqueId() );
	}
	
	private void removePlayerFromCache( TokenCachePlayerData playerData ) {
		
		if ( getPlayers().containsKey( playerData.getPlayer().getUniqueId() ) ) {
			
			getPlayers().remove( playerData.getPlayer().getUniqueId() );
			getPlayerStrings().remove( playerData.getPlayer().getUniqueId().toString() );
		}
	}
	
	protected void submitAsyncDatabaseUpdate( TokenCachePlayerData playerData ) {
		if ( !playerData.isAsyncDatabaseUpdateSubmitted() ) {
			// Submit the async job
			
			if ( playerData.getValueUncommitted() > 0 ) {
				
				TokenCacheUpdateDatabaseTask task = new TokenCacheUpdateDatabaseTask( playerData );
				BukkitTask bTask = getPlugin().getServer().getScheduler().runTaskLaterAsynchronously( 
						getPlugin(), task, (getCacheWriteDelay() / 50) );
				
				// Store the BukkitTask in the playerData for possible future reference.
				playerData.setBuckkitTask( bTask );
				
				getTasks().put( bTask, playerData );
			}
		}
	}
	
	
	protected void submitAsyncLoadPlayer( Player player ) {
	
		TokenCachePlayerData playerData = new TokenCachePlayerData( player );
		
		getPlayers().put( player.getUniqueId(), playerData );
		
		submitAsyncLoadPlayer( playerData );
		
	}
	private void submitAsyncLoadPlayer( TokenCachePlayerData playerData ) {
		
		TokenCacheLoadPlayerTask task = new TokenCacheLoadPlayerTask( playerData );

		// Submit task to run right away:
		BukkitTask bTask = getPlugin().getServer().getScheduler().runTaskLaterAsynchronously( 
						getPlugin(), task, 0 );
		
		// Store the BukkitTask in the playerData for possible future reference.
		playerData.setBuckkitTask( bTask );
		
		getTasks().put( bTask, playerData );
	}
	
	protected void submitAsyncUnloadPlayer( Player player ) {
		
		TokenCachePlayerData playerData = getPlayer( player );
		
		if ( playerData != null ) {
			
			// Remove from the player cache:
			removePlayerFromCache( playerData );
			
			// Save to the database right away with no delay:
			TokenCacheUpdateDatabaseTask task = new TokenCacheUpdateDatabaseTask( playerData );
			BukkitTask bTask = getPlugin().getServer().getScheduler().runTaskLaterAsynchronously( 
					getPlugin(), task, 0 );
			
			// Store the BukkitTask in the playerData for possible future reference.
			playerData.setBuckkitTask( bTask );
			
		}
		
	}
	
	protected void submitAsyncSynchronizePlayers() {
		
		TokenCacheSynchronizeCacheTask task = new TokenCacheSynchronizeCacheTask();
		getPlugin().getServer().getScheduler().runTaskLaterAsynchronously( 
										getPlugin(), task, 0 );
		
	}
	
	public int addTokens( Player player, int tokens ) {
		int results = 0;
		
		if ( isEnabled() ) {
			TokenCachePlayerData playerData = getPlayer( player );
			results = playerData.addTokens( tokens );
			submitAsyncDatabaseUpdate( playerData );
		}
		else {
			// The cache is not enabled, so pass through directly to the database:
			getCacheDatabase().addTokens( player, tokens );
		}
		
		return results;
	}
	
	public int getTokens( Player player ) {
		int tokens = 0;
		
		if ( isEnabled() ) {
			TokenCachePlayerData playerData = getPlayer( player );
			tokens = playerData.getTokens();
		}
		else {
			// The cache is not enabled, so pass through directly to the database:
			getCacheDatabase().getTokens( player );
		}
		
		return tokens;
	}
	
	public void setTokens( Player player, int tokens ) {
		if ( isEnabled() ) {
			TokenCachePlayerData playerData = getPlayer( player );
			playerData.setTokens( tokens );
		}
		else {
			// The cache is not enabled, so pass through directly to the database:
			getCacheDatabase().setTokens( player, tokens );
		}
	}

	public int removeTokens( Player player, int tokens ) {
		return addTokens( player, -1 * tokens );
	}
	
	
	public boolean hasTokens( Player player, int tokens ) {
		boolean results = false;
		
		if ( isEnabled() ) {
			TokenCachePlayerData playerData = getPlayer( player );
			results = playerData.hasTokens( tokens );
		}
		else {
			// The cache is not enabled, so pass through directly to the database:
			results = getTokens( player ) >= tokens;
		}
		
		return results;
	}

	
	
	protected Tokens getPlugin() {
		return plugin;
	}

	public boolean isEnabled() {
		return enabled;
	}
	private void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}

	public long getWriteDelay() {
		return writeDelay;
	}
	public void setWriteDelay( long writeDelay ) {
		this.writeDelay = writeDelay;
	}

	protected Map<UUID, TokenCachePlayerData> getPlayers() {
		return players;
	}

	public TreeMap<String, TokenCachePlayerData> getPlayerStrings() {
		return playerStrings;
	}

	protected TokenCacheDatabase getCacheDatabase() {
		return cacheDatabase;
	}

	public Map<BukkitTask, TokenCachePlayerData> getTasks() {
		return tasks;
	}

}
