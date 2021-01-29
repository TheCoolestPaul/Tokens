package net.thirdshift.tokens.commands;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * This class is used to add sub-commands to Tokens' commands
 *
 * Currently there are two "master commands" "/tokens" and "/redeem"
 *
 */
public abstract class CommandModule {
	protected Tokens plugin;
	protected TokensHandler tokensHandler;
	protected String command;
	protected TokensCustomCommandExecutor parentExecutor;

	public CommandModule(final TokensCustomCommandExecutor executor) {
		plugin = Tokens.getInstance();
		tokensHandler = plugin.getHandler();
		this.parentExecutor = executor;
	}

	/**
	 * Used to give sub-commands permissions
	 * set to null if no permissions
	 * @return permission name
	 */
	public abstract String getPermission();

	public abstract String getDescription();

	/**
	 * Used to inform users about commands
	 * @return The text printed to the commandSender's chat
	 */
	public String getHelpText(){
		return ChatColor.AQUA + getCommandUsage() +
				" " + ChatColor.GRAY + getDescription();
	}

	/**
	 * Used to get the "main" sub command, no aliases.
	 * @return The subcommand
	 */
	public String getCommand(){
		return this.command;
	}

	/**
	 * Used to give a sub command aliases.
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
