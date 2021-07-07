package net.thirdshift.tokens.cache;

import net.thirdshift.tokens.Tokens;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TokenCache {
	
	public static final String TOKEN_CACHE_IS_ENABLED = "tokencache.is_enabled";
	public static final String TOKEN_CACHE_WRITE_DELAY = "tokencache.write_delay";
	public static final long TOKEN_CACHE_WRITE_DELAY_VALUE_MS = 10000; // 10 seconds
	/* Notated out un-used code
	public static final String TOKEN_CACHE_TIME_TO_LIVE = "tokencache.time_to_live";
	public static final long TOKEN_CACHE_TIME_TO_LIVE_VALUE_MS = 30 * 60 * 1000; // 30 mins
	*/

	private static TokenCache instance;
	
	private boolean enabled = false;
	private long writeDelay = 0L;
	
	private TokenCacheStats stats;
	private boolean journal;
	private String journalPlayer;

	
	private final Map<UUID, TokenCachePlayerData> players;
	private final TreeMap<String, TokenCachePlayerData> playerStrings;
	
	private final Map<BukkitTask, TokenCachePlayerData> tasks;
	
	private TokenCacheDatabase cacheDatabase;

	private Tokens plugin;
	
	private TokenCache() {
		super();
		
		this.players = new HashMap<>();
		this.playerStrings = new TreeMap<>();
		
		this.tasks = new HashMap<>();
		
		this.stats = new TokenCacheStats();

	}
	
	public static TokenCache getInstance() {
		return instance;
	}

	public synchronized static void initialize( Tokens plugin ) {
		if ( instance == null ) {
			instance = new TokenCache();
			instance.internalInitialize( plugin );
		}
	}
	
	private void internalInitialize(Tokens plugin ) {
		
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

		getPlugin().getLogger().fine( "TokenCache: " +
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
	 * ensure all data is written to the database before the server is 
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
		getStats().incrementGetPlayers();
		
		if ( !getPlayers().containsKey( player.getUniqueId() ) ) {
			journal( player, "getPlayer: Player not in cache. Loading from DB: " +
					" Player returned from cache will have a token balance of zero until database is loaded");
			
			// Load the player's existing balance:
			submitAsyncLoadPlayer( player );
		}

		// journal( playerData, "getPlayer: Player returned: ");
		return getPlayers().get( player.getUniqueId() );
	}


	private void removePlayerFromCache( TokenCachePlayerData playerData ) {
		getStats().incrementRemovePlayers();
		
		if ( getPlayers().containsKey( playerData.getPlayer().getUniqueId() ) ) {
			
			getPlayers().remove( playerData.getPlayer().getUniqueId() );
			getPlayerStrings().remove( playerData.getPlayer().getUniqueId().toString() );
			journal( playerData, "removePlayerFromCache: ");
		}
	}
	
	protected void submitAsyncDatabaseUpdate( TokenCachePlayerData playerData ) {
		if ( !playerData.isAsyncDatabaseUpdateSubmitted() ) {
			// Submit the async job
			
			if ( playerData.getValueUncommitted() != 0 ) {
				playerData.setAsyncDatabaseUpdateSubmitted( true );
				
				long delay = (getCacheWriteDelay() / 50);
				journal( playerData, "submitAsyncDatabaseUpdate: Submitting. " +
													"delay= " + delay + " ticks. " );
				
				getStats().incrementSubmitDatabaseUpdate();
				
				TokenCacheUpdateDatabaseTask task = new TokenCacheUpdateDatabaseTask( playerData );
				BukkitTask bTask = getPlugin().getServer().getScheduler().runTaskLaterAsynchronously( 
						getPlugin(), task, delay );
				
				// Store the BukkitTask in the playerData for possible future reference.
				playerData.setBuckkitTask( bTask );
				
				getTasks().put( bTask, playerData );
			}
		}
	}
	
	
	protected void submitAsyncLoadPlayer( Player player ) {
		getStats().incrementLoadPlayers();
	
		TokenCachePlayerData playerData = new TokenCachePlayerData( player );
		
		getPlayers().put( player.getUniqueId(), playerData );
		getPlayerStrings().put( player.getUniqueId().toString(), playerData );
		
		
		submitAsyncLoadPlayer( playerData );
		
	}
	private void submitAsyncLoadPlayer( TokenCachePlayerData playerData ) {
		getStats().incrementLoadPlayers();
		
		journal( playerData, "submitAsyncLoadPlayer: ");
		
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
			getStats().incrementUnloadPlayers();
			
			journal( playerData, "submitAsyncUnloadPlayer: ");
			
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
	
	public void submitAsyncSynchronizePlayers() {
		submitAsyncSynchronizePlayers( null );
	}

	public void submitAsyncSynchronizePlayers( CommandSender commandSender ) {
		getStats().incrementSubmitSynchronizePlayers();
		
		TokenCacheSynchronizeCacheTask task = new TokenCacheSynchronizeCacheTask( commandSender );
		getPlugin().getServer().getScheduler().runTaskLaterAsynchronously( 
										getPlugin(), task, 0 );
		
	}
	
	public int addTokens( Player player, int tokens ) {
		int results = 0;
		
		if ( isEnabled() ) {
			getStats().incrementAddTokens();
			TokenCachePlayerData playerData = getPlayer( player );
			results = playerData.addTokens( tokens );
			journal( playerData, "addTokens: ");
			submitAsyncDatabaseUpdate( playerData );
		}
		else {
			// The cache is not enabled, so pass through directly to the database:
			getCacheDatabase().addTokens( player, tokens );
		}
		
		return results;
	}
	
	public void setTokens( Player player, int tokens ) {
		if ( isEnabled() ) {
			getStats().incrementSetTokens();
			TokenCachePlayerData playerData = getPlayer( player );
			playerData.setTokens( tokens );
			journal( playerData, "setTokens: ");
			submitAsyncDatabaseUpdate( playerData );
		}
		else {
			// The cache is not enabled, so pass through directly to the database:
			getCacheDatabase().setTokens( player, tokens );
		}
	}
	
	public int getTokens( Player player ) {
		int tokens;
		
		if ( isEnabled() ) {
			getStats().incrementGetTokens();
			TokenCachePlayerData playerData = getPlayer( player );
			journal( playerData, "getTokens: ");
			tokens = playerData.getTokens();
		}
		else {
			// The cache is not enabled, so pass through directly to the database:
			tokens = getCacheDatabase().getTokens( player );
		}
		
		return tokens;
	}

	public int removeTokens( Player player, int tokens ) {
		getStats().incrementRemoveTokens();
		
		return addTokens( player, -1 * tokens );
	}
	
	
	public boolean hasTokens( Player player, int tokens ) {
		boolean results;
		
		if ( isEnabled() ) {
			getStats().incrementHasTokens();
			TokenCachePlayerData playerData = getPlayer( player );
			journal( playerData, "hasTokens: ");
			results = playerData.hasTokens( tokens );
		}
		else {
			// The cache is not enabled, so pass through directly to the database:
			results = getTokens( player ) >= tokens;
		}
		
		return results;
	}

	public String getPlayerDumpStats() {
		StringBuilder sb = new StringBuilder();

		List<String> keys = new ArrayList<>( getPlayerStrings().keySet() );
		
		for ( String key : keys ) {
			TokenCachePlayerData playerData = getPlayerStrings().get( key );
			
			sb.append( playerData.toString() );
		}
		return sb.toString();
	}
	
	public TokenCacheStats getStats() {
		return stats;
	}
	public void setStats( TokenCacheStats stats ) {
		this.stats = stats;
	}
	
	
	public boolean isJournal() {
		return journal;
	}
	public void setJournal( boolean journal ) {
		this.journal = journal;
	}
	public boolean toggleJournal() {
		return this.journal = !this.journal;
	}
	
	public String getJournalPlayer() {
		return journalPlayer;
	}
	public void setJournalPlayer( String journalPlayer ){
		this.journalPlayer = journalPlayer;
	}

	void journal( TokenCachePlayerData playerData, String message )
	{
		if ( isJournal() && (
				getJournalPlayer() == null || 
				playerData != null &&
				getJournalPlayer().equalsIgnoreCase( playerData.getPlayer().getName() ) )) {
			log( message + (playerData != null ? playerData.toString() : "") );
		}
	}
	private void journal( Player player, String message )
	{
		if ( isJournal() && (
				getJournalPlayer() == null || 
				player != null &&
				getJournalPlayer().equalsIgnoreCase( player.getName() ) )) {
			log( message + (player != null ? player.getName() : "") );
		}
	}

	protected void log( String message ) {
		getPlugin().getLogger().info( message );
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
