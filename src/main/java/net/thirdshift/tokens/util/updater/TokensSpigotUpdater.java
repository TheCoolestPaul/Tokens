package net.thirdshift.tokens.util.updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.semver.BluesSemanticVersionData;
import net.thirdshift.tokens.util.TokensEventListener;

public class TokensSpigotUpdater {
	
	public static final String SPIGOT_API_URL = "https://api.spigotmc.org/legacy/update.php?resource=";
	public static final String SPIGOT_RESOURCE_URL = "https://www.spigotmc.org/resources/";

	private final Tokens plugin;
    private final int projectID;

    private URL spigotCheckURL;
    private String newVersion;

    public TokensSpigotUpdater(Tokens tokens, int projectID) {

    	this.plugin = tokens;
        this.projectID = projectID;
    }

    public Tokens getPlugin() {
        return plugin;
    }

    public String getLatestVersion() {
        return newVersion;
    }
    public void setLatestVersion( String newVersion ) {
    	this.newVersion = newVersion;
    }

    public int getProjectID() {
		return projectID;
	}

	public URL getSpigotCheckURL() {
		return spigotCheckURL;
	}
	public void setSpigotCheckURL( URL spigotCheckURL ) {
		this.spigotCheckURL = spigotCheckURL;
	}

	public String getResourceURL() {
        return SPIGOT_RESOURCE_URL + getProjectID();
    }

    public boolean checkForUpdates() throws Exception {
    	
        try {
        	setSpigotCheckURL( new URL(SPIGOT_API_URL + getProjectID()) );
        } 
        catch (MalformedURLException e) {
            getPlugin().getLogger().warning("TokensSpigotUpdater.checkForUpdates: " + 
            			"Failed to construct the URL to api.spigotmc.org. " +
            			"Something went wrong checking for update");
            return true;
        }
    	
        URLConnection con = getSpigotCheckURL().openConnection();
        try (
        		InputStreamReader streamReader = new InputStreamReader(con.getInputStream());
        		BufferedReader buff = new BufferedReader(streamReader);
        		) {
        	
        	setLatestVersion( buff.readLine() );
        }
        catch ( Exception e ) {
        	getPlugin().getLogger().info("TokensSpigotUpdater.checkForUpdates: " +
        			"Failed to load data from api.spigotmc.org. [" + e.getMessage() + "]");

        	return true;
        }

        String currentVersion = getPlugin().getDescription().getVersion();
        
        BluesSemanticVersionData svCurrentVersion = new BluesSemanticVersionData(currentVersion);
        if ( !svCurrentVersion.isValid() ) {
        	getPlugin().getLogger().info("TokensSpigotUpdater.checkForUpdates: " +
        			"Current version has an invalid semantic version. " +
        			"It is unknown if a newer version is available.");
            return true;
        }
        
        BluesSemanticVersionData svAvailableVersion = new BluesSemanticVersionData(
        											getLatestVersion() );
        if ( !svCurrentVersion.isValid() ) {
        	getPlugin().getLogger().info("TokensSpigotUpdater.checkForUpdates: " +
        			"Available version has an invalid semantic version. It maybe a newer version.");
        	return true;
        }
        
        getPlugin().getLogger().info( "Checking for updates:" +
        			"  Current Version: " + svCurrentVersion.toString() +
        			"  Available Version: " + svAvailableVersion.toString() );

        int compare = svCurrentVersion.compareTo( svAvailableVersion );
        
        if( compare == 0 ) {
        	// Same version: 
        	getPlugin().getLogger().info("Tokens is up to date." );
            return false;
        }
        else if( compare > 0 ) {
        	getPlugin().getLogger().info( "This is a Pre-release version of Tokens " +
        					"(newer than what's available at spigotmc.org).");
            return false;
        }
        else {
        	TokensEventListener teListener = getPlugin().getTokensEventListener();
        	
            if ( !teListener.isOutdated() ){
            	teListener.setOutdated(true);
            	teListener.setUpdateURL(getResourceURL());
            	getPlugin().getLogger().info("We're notifying all operators of an available update on join.");
            } 
            else {
                // I added this if-statement in case there are multiple updates while a server is live and hasn't updated.
                if (!teListener.getUpdateURL().equals(getResourceURL())){
                	teListener.setUpdateURL(getResourceURL());
                }
                getPlugin().getLogger().info("We're already notifying all operators of an available update on join.");
            }

            return true;
        }
    }

//    /**
//     * Compares two version strings.
//     *
//     * Use this instead of String.compareTo() for a non-lexicographical
//     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
//     *
//     * @param v1 a string of alpha numerals separated by decimal points.
//     * @param v2 a string of alpha numerals separated by decimal points.
//     * @return The result is 1 if v1 is greater than v2.
//     *         The result is 2 if v2 is greater than v1.
//     *         The result is -1 if the version format is unrecognized.
//     *         The result is zero if the strings are equal.
//     */
//    public int VersionCompare(String v1,String v2){
//        int v1Len= StringUtils.countMatches(v1,".");
//        int v2Len=StringUtils.countMatches(v2,".");
//
//        if(v1Len!=v2Len)
//        {
//            int count=Math.abs(v1Len-v2Len);
//            if(v1Len>v2Len) {
//                StringBuilder v2Builder = new StringBuilder(v2);
//                for(int i = 1; i<=count; i++)
//                    v2Builder.append(".0");
//                v2 = v2Builder.toString();
//            } else {
//                StringBuilder v1Builder = new StringBuilder(v1);
//                for(int i = 1; i<=count; i++)
//                    v1Builder.append(".0");
//                v1 = v1Builder.toString();
//            }
//        }
//
//        if(v1.equals(v2))
//            return 0;
//
//        String[] v1Str=StringUtils.split(v1, ".");
//        String[] v2Str=StringUtils.split(v2, ".");
//        for(int i=0;i<v1Str.length;i++)
//        {
//            StringBuilder str1= new StringBuilder();
//            StringBuilder str2= new StringBuilder();
//            for (char c : v1Str[i].toCharArray()) {
//                if(Character.isLetter(c))
//                {
//                    int u=c-'a'+1;
//                    if(u<10)
//                        str1.append("0").append(u);
//                    else
//                        str1.append(u);
//                }
//                else
//                    str1.append(c);
//            }
//            for (char c : v2Str[i].toCharArray()) {
//                if(Character.isLetter(c))
//                {
//                    int u=c-'a'+1;
//                    if(u<10)
//                        str2.append("0").append(u);
//                    else
//                        str2.append(u);
//                }
//                else
//                    str2.append(c);
//            }
//            v1Str[i]="1"+str1;
//            v2Str[i]="1"+str2;
//
//            int num1=Integer.parseInt(v1Str[i]);
//            int num2=Integer.parseInt(v2Str[i]);
//
//            if(num1!=num2)
//            {
//                if(num1>num2)
//                    return 1;
//                else
//                    return 2;
//            }
//        }
//        return -1;
//    }

}
