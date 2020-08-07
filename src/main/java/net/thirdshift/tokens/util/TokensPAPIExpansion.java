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

        if(params.equals("getTokens")){
            return Integer.toString(plugin.getHandler().getTokens(player.getPlayer()));
        }else if(params.equals("getTokensFormatted")){
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(plugin.getHandler().getTokens(player.getPlayer()));
        }

        return null;
    }

}
