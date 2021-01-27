package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import net.thirdshift.tokens.messages.messageData.PlayerTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveTokensCommandModule extends CommandModule {

	public GiveTokensCommandModule() {
		this.command="give";
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
				int toGive = Integer.parseInt(args[1]);
				if(tokensHandler.getTokens((Player) commandSender) >= toGive) {
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null) {
						if(target.equals(commandSender)){
							commandSender.sendMessage(ChatColor.RED+"You can't give tokens to yourself.");
						}
						if(toGive <= 0 ){
							commandSender.sendMessage(ChatColor.RED+"You can't do that.");
						}
						plugin.getHandler().removeTokens((Player) commandSender, toGive);
						plugin.getHandler().addTokens(target, toGive);
						PlayerSender sender = new PlayerSender((Player) commandSender);
						PlayerTarget playerTarget = new PlayerTarget(target);
						List<Object> stuff = new ArrayList<>();
						stuff.add(sender);
						stuff.add(playerTarget);
						stuff.add(toGive);
						commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.give.sender", stuff));
						target.sendMessage(plugin.messageHandler.useMessage("tokens.give.receiver", stuff));
					} else {
						if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
							List<Object> objects = new ArrayList<>();
							objects.add(new PlayerSender(commandSender.getName()));
							objects.add(new PlayerTarget(args[0]));
							commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
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
