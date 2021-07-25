package net.thirdshift.tokens.commands.tokens;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageComponents.MessageComponent;
import net.thirdshift.tokens.messages.messageComponents.SenderMessageComponent;
import net.thirdshift.tokens.messages.messageComponents.TargetMessageComponent;
import net.thirdshift.tokens.messages.messageComponents.TokensMessageComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TokensCommandExecutor extends TokensCustomCommandExecutor {

    private final TokensHandler tokensHandler;

    public TokensCommandExecutor(final Tokens plugin){
        super(plugin);
        tokensHandler=plugin.getHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
		if (commandSender instanceof Player) {
			if ( plugin.getTokensCombatManager() != null && plugin.getTokensCombatManager().isInCombat( (Player) commandSender) ) {
				if (!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
					ArrayList<MessageComponent> components = new ArrayList<>();
					components.add(new SenderMessageComponent(commandSender));
					commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", components));
				}
			}
		}
		if (args.length == 0) {
			if (commandSender instanceof Player) {
				if (plugin.messageHandler.getMessage("tokens.main").isEmpty()) {
					return true;
				}
				ArrayList<MessageComponent> components = new ArrayList<>();
				components.add(new SenderMessageComponent(commandSender));
				components.add(new TokensMessageComponent(plugin.getHandler().getTokens((Player) commandSender)));
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.main", components));
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

			for(CommandModule commandModule : commandModules.values()){
				if (commandName.equalsIgnoreCase(commandModule.getCommand())){
					commandModule.onCommand( commandSender, actualArgs);
					return true;
				}
				for(String alias : commandModule.getCommandAliases()){
					if(commandName.equalsIgnoreCase(alias)){
						commandModule.onCommand( commandSender, actualArgs);
						return true;
					}
				}
			}

			// If none of our modules have taken the command, run token player lookup
			if (commandSender.hasPermission("tokens.others") || !(commandSender instanceof Player)) {
				Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					if (!plugin.messageHandler.getMessage("tokens.others").isEmpty()) {
						ArrayList<MessageComponent> components = new ArrayList<>();
						components.add(new TargetMessageComponent(target));
						components.add(new SenderMessageComponent(commandSender));
						components.add(new TokensMessageComponent(tokensHandler.getTokens(target)));
						commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.others", components));
					}
				} else {
					if (!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()) {
						ArrayList<MessageComponent> components = new ArrayList<>();
						components.add(new SenderMessageComponent(commandSender));
						components.add(new TargetMessageComponent(args[0]));
						commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", components));
					}
				}
				return true;
			}
		}
		return false;
    }

}
