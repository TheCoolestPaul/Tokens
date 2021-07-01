package net.thirdshift.tokens.rankup;

import net.thirdshift.tokens.TokensHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class TokenRankup {
    private final TokensHandler handler;

    public TokenRankup(TokensHandler handler, Plugin rankUpPlugin) {
        this.handler = handler;
        RankupPlugin rankup = (RankupPlugin) rankUpPlugin;
        rankup.getRequirements().addRequirements(new TokenRequirement(rankup));
    }

    private class TokenRequirement extends ProgressiveRequirement implements DeductibleRequirement {

        public TokenRequirement(RankupPlugin plugin){
            super(plugin, "tokens");
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
