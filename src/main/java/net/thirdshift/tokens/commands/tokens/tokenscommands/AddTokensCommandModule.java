package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageComponents.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AddTokensCommandModule extends CommandModule {

	public AddTokensCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
	}

	@Override
	public String getPermission() {
		return "tokens.add";
	}

	@Override
	public String getDescription() {
		return "Adds Tokens to a player's balance";
	}

	@Override
	public String getCommand() {
		return "add";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"a"};
	}

	@Override
	public String getCommandUsage() {
		return "/tokens add <player name> <tokens to add>";
	}

	@Override
	public void onCommand(final CommandSender commandSender, final String[] args) {
		if (commandSender instanceof Player){ // Console doesn't need permissions
			if (!commandSender.hasPermission("tokens.add"))
				return;
		}

		if (args.length == 2) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target!=null){
				try {
					int num = Integer.parseInt(args[1]);
					tokensHandler.addTokens(target, num);
					if(!plugin.messageHandler.getMessage("tokens.add.sender").isEmpty()){
						List<MessageComponent> components = new ArrayList<>();
						components.add(new SenderMessageComponent(commandSender));
						components.add(new TokensMessageComponent(num));
						components.add(new TargetMessageComponent(target));
						commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.add.sender", components));
					}
					if(!plugin.messageHandler.getMessage("tokens.add.receiver").isEmpty()){
						List<MessageComponent> components = new ArrayList<>();
						components.add(new SenderMessageComponent(commandSender));
						components.add(new TokensMessageComponent(num));
						components.add(new TargetMessageComponent(target));
						target.sendMessage(plugin.messageHandler.useMessage("tokens.add.receiver", components));
					}
				} catch (NumberFormatException e){
					commandSender.sendMessage(ChatColor.RED + args[1] + " is not a valid number!");
				}
			} else {
				if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
					List<MessageComponent> components = new ArrayList<>();
					components.add(new SenderMessageComponent(commandSender));
					components.add(new TargetMessageComponent(args[0]));
					commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", components));
				}
			}
		} else {
			if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.correction").isEmpty()){
				List<MessageComponent> components = new ArrayList<>();
				components.add(new SenderMessageComponent(commandSender));
				components.add(new CommandMessageComponent(this.getCommandUsage()));
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", components));
			}
		}

	}
}
