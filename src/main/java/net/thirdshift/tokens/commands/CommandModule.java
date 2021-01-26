package net.thirdshift.tokens.commands;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import org.bukkit.command.CommandSender;

public abstract class CommandModule {
	protected Tokens plugin;
	protected TokensHandler tokensHandler;
	protected String command;

	public CommandModule() {
		plugin = Tokens.getInstance();
		tokensHandler = plugin.getHandler();
	}

	public String getCommand(){
		return this.command;
	}

	public abstract String[] getCommandAliases();

	public abstract String getCommandUsage();

	/**
	 * This is ran when a player uses the command
	 * @param commandSender Target player
	 * @param args Array of arguments sent with the command
	 */
	public abstract void onCommand(final CommandSender commandSender, final String[] args);
}
