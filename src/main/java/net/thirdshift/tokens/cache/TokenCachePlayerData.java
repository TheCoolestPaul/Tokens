package net.thirdshift.tokens.cache;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * <p>This class represents the temporal state of a player's balance
 * represented by both the internal (within the database) all the way 
 * through to the uncommitted value.  There are three primary values
 * and they server an important part to maintaining a fast and 
 * reliable cache.
 * </p>
 * 
 * <p>The cached values represent a gated cycle that are designed to 
 * allow for minimal locking, with the fastest response.
 * The following explains the purpose of the different values and
 * hopefully their importance. 
 * </p>
 * 
 * <ul>
 *   <li><b>valueDB</b>: This always represents the value that is 
 *   					 stored in the database.</li>
 *   
 *   <li><b>valueUncommittd</b>: This is value that has yet to be
 *						committed to the database. This value can be
 *						either positive or negative, and when combined
 *						with <b>valueDb</b> and <b>valueTransition</b>
 *						will represent the true balance of the player.
 *						The true balance can never go negative.
 *						</li>
 *
 *   <li><b>valueTransition</b>: To minimize locking, this value is the
 *   					transitional value between the uncommitted amount
 *   					and the database amount.  When writing to the 
 *   					database, it first locks the uncommitted, setting it
 *   					zero, and moving the value to the transition and then
 *   					releases the lock. This value remains active until
 *   					the database can be finalized on it's update, then
 *   					it is added to the valueDB and then set to zero.
 *   					</li>
 * 
 * </ul>
 * 
 * @author RoyalBlueRanger
 *
 */
public class TokenCachePlayerData {

	private Player player;
	
	// Value in database:
	private int valueDB = 0;
	
	// Value in transition: 
	private int valueTransition = 0;
	
	// Value uncommitted:
	private int valueUncommitted = 0;
	
	/**
	 *  This Object lock is used to synchronized the public side of this class
	 *  and the protected side of this class which is the database transaction
	 *  side of things.
	 */
	private Object lock = new Object();
	
	private boolean asyncDatabaseUpdateSubmitted = false;
	
	
	/**
	 * This is just to hold on to the BukkitTask when the database update is submitted.
	 */
	private BukkitTask bukkitTask;
	
	
	
	public TokenCachePlayerData( Player player ) {
		super();
		
		this.player = player;
	}

	
	/**
	 * <p>This function adds the number of tokens to the player.
	 * It uses a lock when adding to the valueUncommitted.
	 * </p>
	 * 
	 * @param tokens
	 */
	public int addTokens( int tokens ) {
		
		synchronized ( lock ) {
			valueUncommitted += tokens;
		}
		
		return valueUncommitted;
	}
	
	public int getTokens() {
		int tokens = 0;
		
		synchronized ( lock ) {
			tokens = valueDB + valueTransition + valueUncommitted;
		}
		
		return tokens;
	}
	
	/**
	 * <p>This function effectively sets the player's balance to the given 
	 * amount.  This is done by taking the total amount that's in the 
	 * database (what's in valueDB) and makes adjustments to that value so
	 * the resulting amount is equal to target token amount.  The valueDB
	 * cannot be changed.
	 * </p>
	 * 
	 * <p>The formula to perform this transaction is you take the target
	 * tokens amount and subtract the valueDB amount, then that is the 
	 * adjustment amount that needs to be stored within the valueUncommited
	 * field.  As simple as this is, we really need to take in to consideration
	 * database updates that may be in progress.  So that modifies the 
	 * calculations that we must perform:
	 * </p>
	 * <pre>valueUncommited = tokens - (valueDB + valueTransition)</pre>
	 * <p>The reason we need to include valueTransition is because if a
	 * database update is in progress, then valueTransition will represent
	 * how the players record will be reflected once the transaction is 
	 * complete.
	 * </p>
	 * 
	 * @param tokens
	 */
	public void setTokens( int tokens ) {
			
		synchronized ( lock ) {
			valueUncommitted = tokens - (valueDB + valueTransition);
		}
	}

	/**
	 * <p>This function removes the number of tokens to the player.
	 * It negates the value of tokens and then calls addTokens.
	 * </p>
	 * 
	 * @param tokens
	 */
	public int removeTokens( int tokens ) {
		return addTokens( -1 * tokens );
	}
	
	
	/**
	 * <p>The player's total token 
	 * @param tokens
	 * @return
	 */
	public boolean hasTokens( int tokens ) {
		boolean results = false;
		
		synchronized ( lock ) {
			results = ( tokens > getTokens());
		}
		
		return results;
	}

	
	protected int databaseStageTokens() {
		int tokens = 0;
		synchronized ( lock ) {
			valueTransition += valueUncommitted;
			valueUncommitted = 0;
			
			tokens = valueTransition;
		}
		return tokens;
	}
	
	
	protected void databaseFinalizeTokens() {
		
		synchronized ( lock ) {
			valueDB += valueTransition;
			valueTransition = 0;
		}
	}

	protected int getValueUncommitted() {
		synchronized ( lock ) {
			return valueUncommitted;
		}
	}
	
	
	protected void setInitialValue( int tokens ) {
		synchronized ( lock ) {
			
			System.err.println("### @@@ TokenCachePlayer: setInitialValue: player: " + 
						getPlayer().getName() + "  valueDB= " + valueDB +
						"  new value= "  + tokens );
			
			valueDB = tokens;
		}
	}
	
	protected Player getPlayer() {
		return player;
	}
	protected void setPlayer( Player player ) {
		this.player = player;
	}
	
	
//	private int getValueDB() {
//		return valueDB;
//	}
//	private void setValueDB( int valueDB ) {
//		this.valueDB = valueDB;
//	}
//
//	private int getValueTransition() {
//		return valueTransition;
//	}
//	private void setValueTransition( int valueTransition ) {
//		this.valueTransition = valueTransition;
//	}

//	private void setValueUncommitted( int valueUncommitted ) {
//		this.valueUncommitted = valueUncommitted;
//	}

	protected boolean isAsyncDatabaseUpdateSubmitted() {
		return asyncDatabaseUpdateSubmitted;
	}
	protected void setAsyncDatabaseUpdateSubmitted( boolean asyncDatabaseUpdateSubmitted ) {
		this.asyncDatabaseUpdateSubmitted = asyncDatabaseUpdateSubmitted;
	}

	public BukkitTask getBukkitTask() {
		return bukkitTask;
	}
	public void setBuckkitTask( BukkitTask bukkitTask ) {
		this.bukkitTask = bukkitTask;
	}
	
	
}
