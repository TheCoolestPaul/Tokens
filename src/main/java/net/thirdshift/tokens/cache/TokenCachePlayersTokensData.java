package net.thirdshift.tokens.cache;

public class TokenCachePlayersTokensData {

	private String uuid;
	private int tokens;
	
	public TokenCachePlayersTokensData( String uuid, int tokens ) {
		super();
		
		this.uuid = uuid;
		this.tokens = tokens;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid( String uuid ) {
		this.uuid = uuid;
	}

	public int getTokens() {
		return tokens;
	}
	public void setTokens( int tokens ) {
		this.tokens = tokens;
	}
}
