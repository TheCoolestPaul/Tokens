package net.thirdshift.tokens.hooks;

import net.thirdshift.tokens.Tokens;

import java.util.HashSet;

public class TokensHookManager {
    private final HashSet<TokensHook> hooks;
    private final Tokens tokens;
    private boolean hasConsumable = false;

    public TokensHookManager(Tokens tokens) {
        this.tokens = tokens;
        hooks = new HashSet<>();
    }

    public void addHook(TokensHook hook){
        if (!hasConsumable && hook.consumesTokens()) {
            hasConsumable = true;
        }
        hooks.add(hook);
    }

    public boolean HasConsumable() {
        return hasConsumable;
    }

    public void removeHook(TokensHook hook) {
        hooks.remove(hook);
    }
}
