package net.thirdshift.tokens.hooks;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;

public abstract class TokensHook {
    protected Tokens tokens;
    protected TokensHandler tokensHandler;

    public TokensHook(Tokens tokens) {
        this.tokens = tokens;
        this.tokensHandler = tokens.getHandler();
    }

    public abstract boolean consumesTokens();
}
