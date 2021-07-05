package net.thirdshift.tokens.hooks.shopguiplus;

import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.provider.economy.EconomyProvider;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import org.bukkit.entity.Player;

public class ShopHook extends TokensHook {
    public ShopHook(Tokens tokens) {
        super(tokens);
        ShopGuiPlusApi.registerEconomyProvider(new TokensShopGUIEconomy());
    }

    @Override
    public boolean consumesTokens() {
        return true;
    }

    private class TokensShopGUIEconomy extends EconomyProvider {
        public TokensShopGUIEconomy() {
            this.currencySuffix = " Tokens";
        }

        @Override
        public String getName() {
            return "Tokens";
        }

        @Override
        public double getBalance(Player player) {
            return tokensHandler.getTokens(player);
        }

        @Override
        public void deposit(Player player, double amount) {
            tokensHandler.addTokens(player, (int) amount);
        }

        @Override
        public void withdraw(Player player, double amount) {
            tokensHandler.removeTokens(player, (int) amount);
        }

        @Override
        public boolean has(Player player, double amount) {
            return tokensHandler.hasEnoughTokens(player, (int) amount);
        }
    }
}
