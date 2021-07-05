package net.thirdshift.tokens.hooks.rankup3;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.events.RankupRegisterEvent;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class RankUpHook extends TokensHook {
    public RankUpHook(Tokens tokens) {
        super(tokens);
        tokens.getServer().getPluginManager().registerEvents(new TokensRankUpListener(), tokens);
    }

    @Override
    public boolean consumesTokens() {
        return true;
    }
    private class TokensRankUpListener implements Listener{
        @EventHandler
        public void onRegisterRanks(RankupRegisterEvent event) {
            event.addRequirement(new TokenRequirement(event.getPlugin()));
            tokens.getLogger().info("Successfully hooked into RankUp3.");
        }
    }

    private class TokenRequirement extends ProgressiveRequirement implements DeductibleRequirement {

        public TokenRequirement(RankupPlugin plugin) {
            super(plugin, "Tokens");
        }
        private TokenRequirement(Requirement clone){
            super(clone);
        }

        @Override
        public void apply(Player player, double multiplier) {
            tokensHandler.removeTokens(player, (int) (getValueDouble()*multiplier));
        }

        @Override
        public double getProgress(Player player) {
            return tokensHandler.getTokens(player);
        }

        @Override
        public Requirement clone() {
            return new TokenRequirement(this);
        }
    }
}
