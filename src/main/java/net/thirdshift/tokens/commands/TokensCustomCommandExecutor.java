package net.thirdshift.tokens.commands;

import net.thirdshift.tokens.Tokens;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class TokensCustomCommandExecutor implements CommandExecutor {

	protected HashMap<String, CommandModule> commandModules;
	protected Tokens plugin;

	public TokensCustomCommandExecutor(final Tokens plugin) {
		commandModules = new HashMap<>();
		this.plugin = plugin;
	}

	public void registerModule(CommandModule commandModule) {
		if (commandModules.get(commandModule.getCommand())!=null){
			plugin.getLogger().warning("A module already exists with command " + commandModule.getCommand());
		} else {
			commandModules.put(commandModule.getCommand(), commandModule);
			plugin.getLogger().info("Added command module "+ commandModule.getCommand());
		}
	}

	public HashMap<String, CommandModule> getCommandModules() {
		return commandModules;
	}

	@Override
	public abstract boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings);
}