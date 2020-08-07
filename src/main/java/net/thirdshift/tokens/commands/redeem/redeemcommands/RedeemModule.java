package net.thirdshift.tokens.commands.redeem.redeemcommands;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import org.bukkit.entity.Player;

public abstract class RedeemModule {
	protected Tokens plugin;
	protected TokensHandler tokensHandler;
	protected String command;

	public RedeemModule() {
		plugin = Tokens.getInstance();
		tokensHandler = plugin.getHandler();
	}

	public abstract String getCommand();

	public abstract String[] getCommandAliases();

	/**
	 * This is ran when a player uses /redeem 'command'
	 * @param player Target player
	 * @param toRedeem Number of tokens to redeem
	 */
	public abstract void redeem(final Player player, final int toRedeem);
}
