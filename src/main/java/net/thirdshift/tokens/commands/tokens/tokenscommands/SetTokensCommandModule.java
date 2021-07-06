package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageComponents.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetTokensCommandModule extends CommandModule {

	public SetTokensCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
	}

	@Override
	public String getPermission() {
		return "tokens.set";
	}

	@Override
	public String getDescription() {
		return "Sets a player's Tokens balance.";
	}

	@Override
	public String getCommand() {
		return "set";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"s"};
	}

	@Override
	public String getCommandUsage() {
		return "/tokens set <player name> <amount to set>";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player){ // Console doesn't need permissions
			if (!commandSender.hasPermission("tokens.set"))
				return;
		}
		List<MessageComponent> components = new ArrayList<>();
		if(args.length==2){
			Player target = Bukkit.getPlayer(args[0]);
			if(target!=null){
				int num;
				try {
					num = Integer.parseInt(args[1]);
				} catch (NumberFormatException e){
					commandSender.sendMessage(args[1]+" is not a valid number!");
					return;
				}
				tokensHandler.setTokens(target, num);
				components.add(new TokensMessageComponent(num));
				components.add(new SenderMessageComponent(commandSender));
				components.add(new TargetMessageComponent(target));
				if (!plugin.messageHandler.useMessage("tokens.set.sender", components).isEmpty())
					commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.set.sender", components));
				if (!plugin.messageHandler.useMessage("tokens.set.receiver", components).isEmpty())
					target.sendMessage(plugin.messageHandler.useMessage("tokens.set.receiver", components));
			} else {
				if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
					components.add(new SenderMessageComponent(commandSender));
					components.add(new TargetMessageComponent(args[0]));
					commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", components));
				}
			}
		} else {
			if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.message").isEmpty()){
				components.add(new SenderMessageComponent(commandSender));
				components.add(new CommandMessageComponent(this.getCommandUsage()));
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.message", components));
			}
			if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.correction").isEmpty()){
				components.add(new SenderMessageComponent(commandSender));
				components.add(new CommandMessageComponent(this.getCommandUsage()));
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", components));
			}
		}
	}
}
