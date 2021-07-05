package net.thirdshift.tokens.hooks.papi;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import net.thirdshift.tokens.hooks.TokensHookRequirement;

public class PapiRequirement extends TokensHookRequirement {
    public PapiRequirement(Tokens tokens) {
        super(tokens, "PlaceHolderAPI");
    }

    @Override
    public TokensHook initHook() {
        return new PapiHook(tokens);
    }
}
