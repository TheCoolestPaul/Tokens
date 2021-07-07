package net.thirdshift.tokens.hooks;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.bsp.BossRequirement;
import net.thirdshift.tokens.hooks.papi.PapiRequirement;
import net.thirdshift.tokens.hooks.picojobs.PicoRequirements;
import net.thirdshift.tokens.hooks.rankup3.RankUpRequirement;
import net.thirdshift.tokens.hooks.shopguiplus.ShopRequirementTokens;

public final class TokensBaseHooks {
    public static void registerBaseHooks(Tokens tokens, TokensHookManager hookManager){
        TokensHookRequirement shopRequirement = new ShopRequirementTokens(tokens);
        if (shopRequirement.hasPassed())
            hookManager.addHook(shopRequirement.initHook());

        TokensHookRequirement picoHook = new PicoRequirements(tokens);
        if (picoHook.hasPassed())
            hookManager.addHook(picoHook.initHook());

        TokensHookRequirement rankupHook = new RankUpRequirement(tokens);
        if (rankupHook.hasPassed())
            hookManager.addHook(rankupHook.initHook());

        TokensHookRequirement papiHook = new PapiRequirement(tokens);
        if (papiHook.hasPassed())
            hookManager.addHook(papiHook.initHook());

        TokensHookRequirement bspHook = new BossRequirement(tokens);
        if (bspHook.hasPassed())
            hookManager.addHook(bspHook.initHook());
    }
}
