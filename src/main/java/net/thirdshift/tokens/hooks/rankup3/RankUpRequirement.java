package net.thirdshift.tokens.hooks.rankup3;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import net.thirdshift.tokens.hooks.TokensHookRequirement;

public class RankUpRequirement extends TokensHookRequirement {
    public RankUpRequirement(Tokens tokens) {
        super(tokens, "Rankup");
    }

    @Override
    public TokensHook initHook() {
        return new RankUpHook(tokens);
    }
}
