package net.thirdshift.tokens.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class PapiHook extends TokensHook {
    public PapiHook(Tokens tokens) {
        super(tokens);
        boolean worked = new TokensPAPI().register();
        if (worked) {
            tokens.getLogger().info("Successfully hooked into PlaceHolderAPI");
        } else {
            tokens.getLogger().warning("Unsuccessfully hooked into PlaceHolderAPI");
        }
    }

    @Override
    public boolean consumesTokens() {
        return false;
    }
    private class TokensPAPI extends PlaceholderExpansion {
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
            return tokens.getDescription().getAuthors().toString();
        }

        @Override
        public @NotNull String getVersion() {
            return tokens.getDescription().getVersion();
        }
        private String request(Player player, String params){
            if (player==null)
                return null;

            if(params.equalsIgnoreCase("getTokens")){
                return Integer.toString(tokensHandler.getTokens(player.getPlayer()));
            }else if(params.equalsIgnoreCase("getTokensFormatted")){
                DecimalFormat formatter = new DecimalFormat("#,###");
                return formatter.format(tokensHandler.getTokens(player.getPlayer()));
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
}
