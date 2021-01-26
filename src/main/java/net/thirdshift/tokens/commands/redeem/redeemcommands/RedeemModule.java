package net.thirdshift.tokens.commands.redeem.redeemcommands;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * This class was made as an attempt to make adding plugin support easier
 */
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

	public abstract String getCommandUsage();

	/**
	 * This is ran when a player uses /redeem 'command'
	 * @param commandSender Target player
	 * @param args Array of arguments sent with the command
	 */
	public abstract void onCommand(final CommandSender commandSender, final ArrayList<String> args);
}
