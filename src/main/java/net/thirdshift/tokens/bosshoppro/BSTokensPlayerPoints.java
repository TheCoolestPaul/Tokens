package net.thirdshift.tokens.bosshoppro;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import org.black_ixx.bossshop.pointsystem.BSPointsPlugin;
import org.bukkit.OfflinePlayer;

public class BSTokensPlayerPoints extends BSPointsPlugin {
	private final TokensHandler tokensHandler;
	public BSTokensPlayerPoints(final Tokens plugin) {
		super("Tokens", "token", "tokens", "Token");
		tokensHandler = plugin.getHandler();
	}

	@Override
	public double getPoints(OfflinePlayer player) {
		return tokensHandler.getTokens(player.getPlayer());
	}

	@Override
	public double setPoints(OfflinePlayer player, double points) {
		tokensHandler.setTokens(player.getPlayer(), (int) points);
		return (int) points;
	}

	@Override
	public double takePoints(OfflinePlayer player, double points) {
		tokensHandler.removeTokens(player.getPlayer(), (int) points);
		return (int) points;
	}

	@Override
	public double givePoints(OfflinePlayer player, double points) {
		tokensHandler.addTokens(player.getPlayer(), (int) points);
		return (int) points;
	}

	@Override
	public boolean usesDoubleValues() {
		return false;
	}
}
