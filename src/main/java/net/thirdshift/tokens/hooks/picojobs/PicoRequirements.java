package net.thirdshift.tokens.hooks.picojobs;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import net.thirdshift.tokens.hooks.TokensHookRequirement;

public class PicoRequirements extends TokensHookRequirement {
    public PicoRequirements(Tokens tokens) {
        super(tokens, "PicoJobs");
    }

    @Override
    public TokensHook initHook() {
        return new PicoHook(tokens);
    }
}
