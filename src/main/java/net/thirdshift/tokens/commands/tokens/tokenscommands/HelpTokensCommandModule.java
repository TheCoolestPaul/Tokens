package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpTokensCommandModule extends CommandModule {

	public HelpTokensCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
		this.command = "help";
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Displays this helpful message.";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"h","?"};
	}

	@Override
	public String getCommandUsage() {
		return "/tokens help";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {
		boolean isConsole = !(commandSender instanceof Player);

		commandSender.sendMessage(ChatColor.GREEN + "===============[ " + ChatColor.GOLD + "Tokens Help " +
				ChatColor.BLUE + plugin.getDescription().getVersion() + ChatColor.GREEN + " ]===============");
		commandSender.sendMessage(ChatColor.AQUA + "/tokens help " + ChatColor.GRAY + " Displays this helpful text");
		commandSender.sendMessage(ChatColor.AQUA + "/tokens" + ChatColor.GRAY + " Displays your number of tokens");

		for (CommandModule module : parentExecutor.getCommandModules().values()){
			if ( module.getPermission() == null || commandSender.hasPermission(module.getPermission()) || isConsole) {
				commandSender.sendMessage(module.getHelpText());
			}
		}

		//if (commandSender.hasPermission("tokens.add") || isConsole) {
		//	commandSender.sendMessage(ChatColor.AQUA + "/tokens add <player name> <tokens amount>" + ChatColor.GRAY + " Adds tokens to a player");
		//}
		//
		//commandSender.sendMessage(ChatColor.AQUA + "/tokens give <player name> <tokens amount>" + ChatColor.GRAY + " Gives your tokens to another player");
		//if(commandSender.hasPermission("tokens.remove") || isConsole) {
		//	commandSender.sendMessage(ChatColor.AQUA + "/tokens remove <player name> <tokens amount>" + ChatColor.GRAY + " Remove tokens from a player");
		//}

		commandSender.sendMessage(ChatColor.AQUA + "/redeem" + ChatColor.GRAY + " Displays help for using the redeem commands");
		if(commandSender.hasPermission("tokens.cache") || isConsole) {
			commandSender.sendMessage(ChatColor.AQUA + "/tokens cache " + ChatColor.GRAY + " Displays the token cache sub-commands");
		}
	}
}
