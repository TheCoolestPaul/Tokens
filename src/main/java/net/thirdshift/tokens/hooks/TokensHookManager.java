package net.thirdshift.tokens.hooks;

import java.util.HashSet;

public class TokensHookManager {
    private final HashSet<TokensHook> hooks;
    private boolean hasConsumable = false;

    public TokensHookManager() {
        hooks = new HashSet<>();
    }

    public void addHook(TokensHook hook){
        if (!hasConsumable && hook.consumesTokens()) {
            hasConsumable = true;
        }
        hooks.add(hook);
    }

    public HashSet<TokensHook> getHooks() {
        return hooks;
    }

    public boolean HasConsumable() {
        return hasConsumable;
    }

    public void removeHook(TokensHook hook) {
        hooks.remove(hook);
    }
}
