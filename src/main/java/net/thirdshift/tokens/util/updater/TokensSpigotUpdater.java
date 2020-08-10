package net.thirdshift.tokens.util.updater;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.util.TokensEventListener;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TokensSpigotUpdater {

    private final int project;
    private URL checkURL;
    private String newVersion;
    private final JavaPlugin plugin;
    private TokensEventListener tokensEventListener;

    public TokensSpigotUpdater(Tokens tokens, int projectID) {
        this.plugin = tokens;
        tokensEventListener=tokens.getTokensEventListener();
        this.newVersion = plugin.getDescription().getVersion();
        this.project = projectID;
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
            plugin.getLogger().warning("Something went wrong checking for update");
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public String getLatestVersion() {
        return newVersion;
    }

    public String getResourceURL() {
        return "https://www.spigotmc.org/resources/" + project;
    }

    public boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        InputStreamReader streamReader = null;
        BufferedReader buff = null;
        try {
            streamReader = new InputStreamReader(con.getInputStream());
            buff = new BufferedReader(streamReader);
            this.newVersion = buff.readLine();
        }finally{
            if(streamReader!=null)
                streamReader.close();
            if(buff!=null)
                buff.close();
        }
        String insVer = plugin.getDescription().getVersion();
        int compare = VersionCompare(insVer, newVersion);
        if(compare==0) {
            return false;
        }else if(compare==1){
            plugin.getLogger().info("Pre-release version");
            return false;
        }else if(compare==2){
            if ( !tokensEventListener.isOutdated() ){
                tokensEventListener.setOutdated(true);
                tokensEventListener.setUpdateURL(getResourceURL());
                plugin.getLogger().info("We're notifying all operators of an available update on join.");
            } else {
                // I added this if-statement in case there are multiple updates while a server is live and hasn't updated.
                if (!tokensEventListener.getUpdateURL().equals(getResourceURL())){
                    tokensEventListener.setUpdateURL(getResourceURL());
                }
                plugin.getLogger().info("We're already notifying all operators of an available update on join.");
            }

            return true;
        }else{
            plugin.getLogger().info("An error occurred in the updater");
            return true;
        }
    }

    /**
     * Compares two version strings.
     *
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param v1 a string of alpha numerals separated by decimal points.
     * @param v2 a string of alpha numerals separated by decimal points.
     * @return The result is 1 if v1 is greater than v2.
     *         The result is 2 if v2 is greater than v1.
     *         The result is -1 if the version format is unrecognized.
     *         The result is zero if the strings are equal.
     */
    public int VersionCompare(String v1,String v2){
        int v1Len= StringUtils.countMatches(v1,".");
        int v2Len=StringUtils.countMatches(v2,".");

        if(v1Len!=v2Len)
        {
            int count=Math.abs(v1Len-v2Len);
            if(v1Len>v2Len) {
                StringBuilder v2Builder = new StringBuilder(v2);
                for(int i = 1; i<=count; i++)
                    v2Builder.append(".0");
                v2 = v2Builder.toString();
            } else {
                StringBuilder v1Builder = new StringBuilder(v1);
                for(int i = 1; i<=count; i++)
                    v1Builder.append(".0");
                v1 = v1Builder.toString();
            }
        }

        if(v1.equals(v2))
            return 0;

        String[] v1Str=StringUtils.split(v1, ".");
        String[] v2Str=StringUtils.split(v2, ".");
        for(int i=0;i<v1Str.length;i++)
        {
            StringBuilder str1= new StringBuilder();
            StringBuilder str2= new StringBuilder();
            for (char c : v1Str[i].toCharArray()) {
                if(Character.isLetter(c))
                {
                    int u=c-'a'+1;
                    if(u<10)
                        str1.append("0").append(u);
                    else
                        str1.append(u);
                }
                else
                    str1.append(c);
            }
            for (char c : v2Str[i].toCharArray()) {
                if(Character.isLetter(c))
                {
                    int u=c-'a'+1;
                    if(u<10)
                        str2.append("0").append(u);
                    else
                        str2.append(u);
                }
                else
                    str2.append(c);
            }
            v1Str[i]="1"+str1;
            v2Str[i]="1"+str2;

            int num1=Integer.parseInt(v1Str[i]);
            int num2=Integer.parseInt(v2Str[i]);

            if(num1!=num2)
            {
                if(num1>num2)
                    return 1;
                else
                    return 2;
            }
        }
        return -1;
    }

}
