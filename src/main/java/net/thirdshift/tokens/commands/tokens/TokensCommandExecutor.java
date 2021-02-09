package net.thirdshift.tokens.commands.tokens;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import net.thirdshift.tokens.messages.messageData.PlayerTarget;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TokensCommandExecutor extends TokensCustomCommandExecutor {

    private final TokensHandler tokensHandler;

    public TokensCommandExecutor(final Tokens plugin){
        super(plugin);
        tokensHandler=plugin.getHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
		if (commandSender instanceof Player) {
			if (plugin.getTokensConfigHandler().isRunningCombatLogX() && plugin.getTokensConfigHandler().getTokensCombatManager().isInCombat((Player) commandSender)) {
				if (!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
					List<Object> objects = new ArrayList<>();
					objects.add(new PlayerSender((Player) commandSender));
					commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", objects));
				}
			}
		}
		if (args.length == 0) {
			if (commandSender instanceof Player) {
				if (plugin.messageHandler.getMessage("tokens.main").isEmpty()) {
					return true;
				}
				List<Object> objects = new ArrayList<>();
				objects.add(new PlayerSender((Player) commandSender));
				objects.add(plugin.getHandler().getTokens((Player) commandSender));
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.main", objects));
			}
			return true;
		} else {
			String commandName = args[0];
			String[] actualArgs = new String[args.length-1];
			System.arraycopy(args, 1, actualArgs, 0, args.length - 1);

			try {
				commandModules.get(commandName).onCommand(commandSender, actualArgs);
				return true;
			} catch (NullPointerException ignored){
			}

			for(CommandModule redeemCommandModule : commandModules.values()){
				if (commandName.equalsIgnoreCase(redeemCommandModule.getCommand())){
					redeemCommandModule.onCommand( commandSender, actualArgs);
					return true;
				}
				for(String alias : redeemCommandModule.getCommandAliases()){
					if(commandName.equalsIgnoreCase(alias)){
						redeemCommandModule.onCommand( commandSender, actualArgs);
						return true;
					}
				}
			}

			// If none of our modules have taken the command, run token player lookup
			if (commandSender.hasPermission("tokens.others") || !(commandSender instanceof Player)) {
				Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					if (!plugin.messageHandler.getMessage("tokens.others").isEmpty()) {
						List<Object> objects = new ArrayList<>();
						objects.add(new PlayerTarget(target));
						objects.add(new PlayerSender(commandSender));
						objects.add(tokensHandler.getTokens(target));
						commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.others", objects));
					}
				} else {
					if (!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()) {
						List<Object> objects = new ArrayList<>();
						objects.add(new PlayerSender(commandSender));
						objects.add(new PlayerTarget(args[0]));
						commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
					}
				}
				return true;
			}
		}
		return false;
    }

}
