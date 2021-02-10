package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.cache.TokenCache;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadTokensCommandModule extends CommandModule {

	public ReloadTokensCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
	}

	@Override
	public String getPermission() {
		return "tokens.reload";
	}

	@Override
	public String getDescription() {
		return "Reloads the Tokens plugin.";
	}

	@Override
	public String getCommand() {
		return "reload";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[0];
	}

	@Override
	public String getCommandUsage() {
		return "/tokens reload";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {
		if( !(commandSender instanceof Player) || commandSender.hasPermission("tokens.reload") ) {
			if (args.length == 0) {
				// Shutdown the token cache prior to reloading:
				TokenCache.onDisable();
				plugin.reloadConfig();
				commandSender.sendMessage(ChatColor.GRAY + "Reloaded all the config files");
			} else if (args[0].equalsIgnoreCase("keys")) {
				plugin.reloadKeys();
				commandSender.sendMessage(ChatColor.GRAY+"Reloaded the key config");
			} else if (args[0].equalsIgnoreCase("messages")) {
				plugin.reloadMessages();
				commandSender.sendMessage(ChatColor.GRAY+"Reloaded the messages config");
			} else if (args[0].equalsIgnoreCase("cache")) {
				// Shutdown the token cache prior to reloading:
				TokenCache.onDisable();
				commandSender.sendMessage(ChatColor.GRAY+"Reloaded the token cache");
			} else {
				commandSender.sendMessage(ChatColor.RED+"Invalid reload command!");
				commandSender.sendMessage(ChatColor.GRAY+"Use /tokens reload <keys | messages | cache>");
			}
		}
	}
}
