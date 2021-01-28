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

	public abstract String getPermission();

	public String getCommand(){
		return this.command;
	}

	/**
	 * Used to give a command module aliases.
	 * @return Aliases for the command
	 */
	public abstract String[] getCommandAliases();

	/**
	 * This method is used to explain to the player how to use the command module.
	 * @return String printed to the player's chat.
	 */
	public abstract String getCommandUsage();

	/**
	 * This is ran when a player uses the command
	 * @param commandSender Target player
	 * @param args Array of arguments sent with the command
	 */
	public abstract void onCommand(final CommandSender commandSender, final String[] args);
}
