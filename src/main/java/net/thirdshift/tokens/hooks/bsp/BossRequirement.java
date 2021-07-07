package net.thirdshift.tokens.hooks.bsp;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import net.thirdshift.tokens.hooks.TokensHookRequirement;

public class BossRequirement extends TokensHookRequirement {
    public BossRequirement(Tokens tokens) {
        super(tokens, "BossShopPro");
    }

    @Override
    public TokensHook initHook() {
        return new BossShopHook(tokens);
    }
}
