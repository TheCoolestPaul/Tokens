package net.thirdshift.tokens.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thirdshift.tokens.Tokens;
import org.bukkit.entity.Player;

public class TokensPAPIExpansion extends PlaceholderExpansion {

    private Tokens plugin;

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
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        // %tokens_getTokens%
        if(identifier.equals("getTokens")){
            return String.valueOf(plugin.handler.getTokens(player));
        }


        return null;
    }

}
