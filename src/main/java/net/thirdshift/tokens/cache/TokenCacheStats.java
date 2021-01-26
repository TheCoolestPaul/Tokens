package net.thirdshift.tokens.cache;

public class TokenCacheStats {

	private long startMs;
	
	private boolean enabled = false;
	
	private int loadPlayer = 0;
	private int unloadPlayer = 0;
	private int removePlayer = 0;
	private int getPlayer = 0;

	private int submitDatabaseUpdate = 0;
	private int synchronizePlayers = 0;
	
	private int addTokens = 0;
	private int getTokens = 0;
	private int setTokens = 0;
	private int removeTokens = 0;
	private int hasTokens = 0;
	
	private Object lock1 = new Object();
	private Object lock2 = new Object();
	private Object lock3 = new Object();
	private Object lock4 = new Object();
	private Object lock5 = new Object();
	private Object lock6 = new Object();
	private Object lock7 = new Object();
	private Object lock8 = new Object();
	private Object lock9 = new Object();
	private Object lockA = new Object();
	private Object lockB = new Object();
	
	public TokenCacheStats() {
		super();
		
		this.startMs = System.currentTimeMillis();
	}
	
	/**
	 * <p>If enabled, stats will be collected.
	 * </p>
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}
	public void toggleEnabled() {
		this.enabled = !this.enabled;
	}



	public String displayStats() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "TokenCache stats: loadPlayer=" ).append( getLoadPlayer() )
			.append( " unloadPlayer=" ).append( getUnloadPlayer() )
			.append( " removePlayer=" ).append( getRemovePlayer() )
			.append( " getPlayer=" ).append( getGetPlayer() )
			
			.append( " submitDatabaseUpdate=" ).append( getSubmitDatabaseUpdate() )
			.append( " synchronizeDatabase=" ).append( getSynchronizePlayers() )
			
			.append( " addTokens=" ).append( getAddTokens() )
			.append( " getTokens=" ).append( getGetTokens() )
			.append( " setTokens=" ).append( getSetTokens() )
			.append( " removeTokens=" ).append( getRemoveTokens() )
			.append( " hasTokens=" ).append( getHasTokens() );
		
		return sb.toString();
	}
	
	public void clear() {
		
		setStartMs( System.currentTimeMillis() );

		setLoadPlayer( 0 );
		setUnloadPlayer( 0 );
		setRemovePlayer( 0 );
		setGetPlayer( 0 );
		
		setSubmitDatabaseUpdate( 0 );
		setSynchronizePlayers( 0 );
		
		setAddTokens( 0 );
		setGetTokens( 0 );
		setSetTokens( 0 );
		setRemoveTokens( 0 );
		setHasTokens( 0 );
		
	}
	
	public void incrementGetPlayers() {
		if ( enabled ) {
			synchronized ( lock1 ) {
				getPlayer++;
			}
		}
	}
	public void incrementRemovePlayers() {
		if ( enabled ) {
			synchronized ( lock2 ) {
				removePlayer++;
			}
		}
	}
	public void incrementLoadPlayers() {
		if ( enabled ) {
			synchronized ( lock3 ) {
				loadPlayer++;
			}
		}
	}
	public void incrementUnloadPlayers() {
		if ( enabled ) {
			synchronized ( lock4 ) {
				unloadPlayer++;
			}
		}
	}
	
	
	public void incrementSubmitDatabaseUpdate() {
		if ( enabled ) {
			synchronized ( lock5 ) {
				submitDatabaseUpdate++;
			}
		}
	}
	public void incrementSubmitSynchronizePlayers() {
		if ( enabled ) {
			synchronized ( lockB ) {
				synchronizePlayers++;
			}
		}
	}
	
	
	public void incrementAddTokens() {
		if ( enabled ) {
			synchronized ( lock6 ) {
				addTokens++;
			}
		}
	}
	public void incrementGetTokens() {
		if ( enabled ) {
			synchronized ( lock7 ) {
				getTokens++;
			}
		}
	}
	public void incrementSetTokens() {
		if ( enabled ) {
			synchronized ( lock8 ) {
				setTokens++;
			}
		}
	}
	public void incrementRemoveTokens() {
		if ( enabled ) {
			synchronized ( lock9 ) {
				removeTokens++;
			}
		}
	}
	public void incrementHasTokens() {
		if ( enabled ) {
			synchronized ( lockA ) {
				hasTokens++;
			}
		}
	}
	
	public long getStartMs() {
		return startMs;
	}
	public void setStartMs( long startMs ) {
		this.startMs = startMs;
	}
	
	public int getGetPlayer() {
		return getPlayer;
	}
	public void setGetPlayer( int getPlayer ) {
		this.getPlayer = getPlayer;
	}
	
	public int getRemovePlayer() {
		return removePlayer;
	}
	public void setRemovePlayer( int removePlayer ) {
		this.removePlayer = removePlayer;
	}
	
	public int getLoadPlayer() {
		return loadPlayer;
	}
	public void setLoadPlayer( int loadPlayer ) {
		this.loadPlayer = loadPlayer;
	}
	
	public int getUnloadPlayer() {
		return unloadPlayer;
	}
	public void setUnloadPlayer( int unloadPlayer ) {
		this.unloadPlayer = unloadPlayer;
	}
	
	public int getSubmitDatabaseUpdate() {
		return submitDatabaseUpdate;
	}
	public void setSubmitDatabaseUpdate( int submitDatabaseUpdate ) {
		this.submitDatabaseUpdate = submitDatabaseUpdate;
	}
	
	public int getSynchronizePlayers() {
		return synchronizePlayers;
	}
	public void setSynchronizePlayers( int synchronizePlayers ) {
		this.synchronizePlayers = synchronizePlayers;
	}

	public int getAddTokens() {
		return addTokens;
	}
	public void setAddTokens( int addTokens ) {
		this.addTokens = addTokens;
	}
	
	public int getGetTokens() {
		return getTokens;
	}
	public void setGetTokens( int getTokens ) {
		this.getTokens = getTokens;
	}
	
	public int getSetTokens() {
		return setTokens;
	}
	public void setSetTokens( int setTokens ) {
		this.setTokens = setTokens;
	}
	
	public int getRemoveTokens() {
		return removeTokens;
	}
	public void setRemoveTokens( int removeTokens ) {
		this.removeTokens = removeTokens;
	}
	
	public int getHasTokens() {
		return hasTokens;
	}
	public void setHasTokens( int hasTokens ) {
		this.hasTokens = hasTokens;
	}
	
}
