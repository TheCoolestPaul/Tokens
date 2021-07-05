package net.thirdshift.tokens.hooks;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.picojobs.PicoRequirements;
import net.thirdshift.tokens.hooks.shopguiplus.ShopRequirementTokens;

public final class TokensBaseHooks {
    public static void registerBaseHooks(Tokens tokens, TokensHookManager hookManager){
        TokensHookRequirement shopRequirement = new ShopRequirementTokens(tokens);
        if (shopRequirement.hasPassed())
            hookManager.addHook(shopRequirement.initHook());

        TokensHookRequirement picoHook = new PicoRequirements(tokens);
        if (picoHook.hasPassed())
            hookManager.addHook(picoHook.initHook());
    }
}
