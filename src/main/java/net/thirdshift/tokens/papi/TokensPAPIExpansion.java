package net.thirdshift.tokens.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thirdshift.tokens.Tokens;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class TokensPAPIExpansion extends PlaceholderExpansion {

    private final Tokens plugin;

    public TokensPAPIExpansion(Tokens instance){ this.plugin=instance; }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tokens";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    private String request(Player player, String params){
        if (player==null)
            return null;

        if(params.equalsIgnoreCase("getTokens")){
            return Integer.toString(plugin.getHandler().getTokens(player.getPlayer()));
        }else if(params.equalsIgnoreCase("getTokensFormatted")){
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(plugin.getHandler().getTokens(player.getPlayer()));
        }
        return null;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
       return request(player.getPlayer(), params);
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return request(player, params);
    }
}
