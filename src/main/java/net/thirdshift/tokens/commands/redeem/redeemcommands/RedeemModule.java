package net.thirdshift.tokens.commands.redeem.redeemcommands;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class RedeemModule {
	protected Tokens plugin;
	protected TokensHandler tokensHandler;
	protected String command;
	protected String commandUsage;

	public RedeemModule() {
		plugin = Tokens.getInstance();
		tokensHandler = plugin.getHandler();
	}

	public abstract String getCommand();

	public abstract String[] getCommandAliases();

	public abstract String getCommandUsage();

	/**
	 * This is ran when a player uses /redeem 'command'
	 * @param player Target player
	 * @param args Array of arguments sent with the command
	 */
	public abstract void redeem(final Player player, final ArrayList<String> args);
}
