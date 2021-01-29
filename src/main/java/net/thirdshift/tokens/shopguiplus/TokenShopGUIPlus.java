package net.thirdshift.tokens.shopguiplus;

import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.provider.economy.EconomyProvider;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import org.bukkit.entity.Player;

/**
 * This class is a wrapper to integrate ShopGUIPlus
 * functionality into Tokens' very own TokensHandler class
 */
public class TokenShopGUIPlus extends EconomyProvider {

	private final TokensHandler tokensHandler;

	public TokenShopGUIPlus(final Tokens plugin){
		tokensHandler = plugin.getHandler();
		this.currencySuffix = " Tokens";
		ShopGuiPlusApi.registerEconomyProvider(this);
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
