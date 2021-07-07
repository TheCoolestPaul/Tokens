package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageComponents.MessageComponent;
import net.thirdshift.tokens.messages.messageComponents.SenderMessageComponent;
import net.thirdshift.tokens.messages.messageComponents.TargetMessageComponent;
import net.thirdshift.tokens.messages.messageComponents.TokensMessageComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveTokensCommandModule extends CommandModule {

	public GiveTokensCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
	}

	@Override
	public String getPermission() {
		return "tokens.give";
	}

	@Override
	public String getDescription() {
		return "Give your Tokens to another player";
	}

	@Override
	public String getCommand() {
		return "give";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"g"};
	}

	@Override
	public String getCommandUsage() {
		return "/tokens give <target player> <amount to give>";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {
		if(commandSender instanceof Player && commandSender.hasPermission("tokens.give")){
			if(args.length==2){
				int toGive;
				try {
					toGive = Integer.parseInt(args[1]);
				} catch (NumberFormatException e){
					commandSender.sendMessage(args[1]+" isn't a valid number!");
					return;
				}
				if(tokensHandler.hasEnoughTokens((Player) commandSender, toGive)) {
					Player target = Bukkit.getPlayer(args[0]);
					if (target != null) {
						if(target.equals(commandSender)){
							commandSender.sendMessage(ChatColor.RED+"You can't give tokens to yourself.");
							return;
						}
						if(toGive <= 0 ){
							commandSender.sendMessage(ChatColor.RED+"You can't do that.");
							return;
						}
						plugin.getHandler().removeTokens((Player) commandSender, toGive);
						plugin.getHandler().addTokens(target, toGive);
						List<MessageComponent> components = new ArrayList<>();
						components.add(new SenderMessageComponent(commandSender));
						components.add(new TargetMessageComponent(target));
						components.add(new TokensMessageComponent(toGive));
						commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.give.sender", components));
						target.sendMessage(plugin.messageHandler.useMessage("tokens.give.receiver", components));
					} else {
						if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
							List<MessageComponent> components = new ArrayList<>();
							components.add(new SenderMessageComponent(commandSender));
							components.add(new TargetMessageComponent(args[0]));
							commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", components));
						}
					}
				}else{
					commandSender.sendMessage(ChatColor.GRAY+"You don't have "+ChatColor.GOLD+""+toGive+""+ChatColor.GRAY+" tokens.");
				}
			}else{
				commandSender.sendMessage(ChatColor.RED+"Invalid command use! Your arguments were "+ Arrays.toString(args));
				commandSender.sendMessage(ChatColor.GRAY+"Command usage: /tokens give <player name> <tokens amount>");
			}
		}
	}
}
