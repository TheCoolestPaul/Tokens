package net.thirdshift.tokens.hooks.shopguiplus;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import net.thirdshift.tokens.hooks.TokensHookRequirement;

public class ShopRequirementTokens extends TokensHookRequirement {

    public ShopRequirementTokens(Tokens tokens) {
        super(tokens, "ShopGUIPlus");
    }
    @Override
    public TokensHook initHook() {
        return new ShopHook(tokens);
    }
}
