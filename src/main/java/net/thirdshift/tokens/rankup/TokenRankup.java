package net.thirdshift.tokens.rankup;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.events.RankupRegisterEvent;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class TokenRankup {
    private final Tokens plugin;
    private final TokensHandler handler;

    public TokenRankup(Tokens plugin) {
        this.plugin = plugin;
        this.handler = plugin.getHandler();
        plugin.getServer().getPluginManager().registerEvents(new TokensRankupListener(), plugin);
    }

    private class TokensRankupListener implements Listener {
        @EventHandler
        public void onRegisterRanks(RankupRegisterEvent event){
            event.addRequirement(new TokenRequirement(event.getPlugin()));
            plugin.getLogger().info("Successfully hooked into RankUp.");
        }
    }

    private class TokenRequirement extends ProgressiveRequirement implements DeductibleRequirement {

        public TokenRequirement(RankupPlugin plugin){
            super(plugin, "Tokens");
        }

        private TokenRequirement(Requirement clone){
            super(clone);
        }

        @Override
        public double getProgress(Player player) {
            return handler.getTokens(player);
        }

        @Override
        public Requirement clone() {
            return new TokenRequirement(this);
        }

        @Override
        public void apply(Player player, double multiplier) {
            handler.removeTokens(player, (int) (getValueDouble()*multiplier));
        }
    }
}
