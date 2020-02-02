package net.thirdshift.tokens.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TokensSpigotUpdater {

    private int project = 0;
    private URL checkURL;
    private String newVersion = "";
    private JavaPlugin plugin;

    public TokensSpigotUpdater(JavaPlugin tokens, int projectID) {
        this.plugin = tokens;
        this.newVersion = plugin.getDescription().getVersion();
        this.project = projectID;
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
            plugin.getLogger().warning("Something went wrong checking for update");
        }
    }

    public int getProjectID() {
        return project;
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
        this.newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        String insVer = plugin.getDescription().getVersion();
        int compare = VersionCompare(insVer, newVersion);
        if(compare==0) {
            return false;
        }else if(compare==1){
            plugin.getLogger().info("Pre-release version");
            return false;
        }else if(compare==2){
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
            if(v1Len>v2Len)
                for(int i=1;i<=count;i++)
                    v2+=".0";
            else
                for(int i=1;i<=count;i++)
                    v1+=".0";
        }

        if(v1.equals(v2))
            return 0;

        String[] v1Str=StringUtils.split(v1, ".");
        String[] v2Str=StringUtils.split(v2, ".");
        for(int i=0;i<v1Str.length;i++)
        {
            String str1="",str2="";
            for (char c : v1Str[i].toCharArray()) {
                if(Character.isLetter(c))
                {
                    int u=c-'a'+1;
                    if(u<10)
                        str1+=String.valueOf("0"+u);
                    else
                        str1+=String.valueOf(u);
                }
                else
                    str1+=String.valueOf(c);
            }
            for (char c : v2Str[i].toCharArray()) {
                if(Character.isLetter(c))
                {
                    int u=c-'a'+1;
                    if(u<10)
                        str2+=String.valueOf("0"+u);
                    else
                        str2+=String.valueOf(u);
                }
                else
                    str2+=String.valueOf(c);
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
