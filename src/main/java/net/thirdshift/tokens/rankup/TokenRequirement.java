package net.thirdshift.tokens.rankup;


import net.thirdshift.tokens.TokensHandler;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class TokenRequirement extends ProgressiveRequirement implements DeductibleRequirement {

    TokensHandler handler;

    public TokenRequirement(TokensHandler handler, Rankup rankup){
        this(rankup);
        this.handler = handler;
    }

    private TokenRequirement(Rankup plugin){
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
