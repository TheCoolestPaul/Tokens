package net.thirdshift.tokens.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thirdshift.tokens.Tokens;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;

public class TokensPAPIExpansion extends PlaceholderExpansion {

    private final Tokens plugin;

    public TokensPAPIExpansion(Tokens instance){ this.plugin=instance; }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "tokens";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(player==null){
            return null;
        }
        if(getIdentifier().equals("getTokens")){ // %tokens_getTokens%
            return String.valueOf(plugin.handler.getTokens(player.getPlayer()));
        }else if(getIdentifier().equals("getTokens_Formatted")){ // %tokens_getTokens_Formatted%
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(plugin.handler.getTokens(player.getPlayer()));
        }
        return null;
    }

}
