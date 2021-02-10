package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import net.thirdshift.tokens.messages.messageData.PlayerTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemoveTokensCommandModule extends CommandModule {

	public RemoveTokensCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
	}

	@Override
	public String getPermission() {
		return "tokens.remove";
	}

	@Override
	public String getDescription() {
		return "Removes Tokens from a player's balance.";
	}

	@Override
	public String getCommand() {
		return "remove";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"r"};
	}

	@Override
	public String getCommandUsage() {
		return "/tokens remove <target player> <amount to remove>";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {
		if ( commandSender instanceof Player && !commandSender.hasPermission("tokens.remove"))
			return;

		if(args.length==2){
			Player target = Bukkit.getPlayer(args[0]);
			if(target!=null){
				int num;
				try {
					num = Integer.parseInt(args[1]);
				} catch (NumberFormatException e){
					commandSender.sendMessage(ChatColor.RED+"Invalid command use!");
					commandSender.sendMessage(getCommandUsage());
					return;
				}
				plugin.getHandler().removeTokens(target, num);
				List<Object> objects = new ArrayList<>();
				objects.add(new PlayerSender(commandSender));
				objects.add(new PlayerTarget(target));
				objects.add(num);
				if(!plugin.messageHandler.getMessage("tokens.remove.sender").isEmpty()){
					commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.remove.sender", objects));
				}
				if(!plugin.messageHandler.getMessage("tokens.remove.receiver").isEmpty()){
					target.sendMessage(plugin.messageHandler.useMessage("tokens.remove.receiver", objects));
				}
			}else{
				if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
					List<Object> objects = new ArrayList<>();
					objects.add(new PlayerSender(commandSender));
					objects.add(new PlayerTarget(args[0]));
					commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
				}
			}
		}else{
			if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.message").isEmpty()){
				List<Object> objects = new ArrayList<>();
				objects.add(new PlayerSender(commandSender));
				objects.add("/tokens remove <player name> <tokens amount>");
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.message", objects));
			}
			if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.correction").isEmpty()){
				List<Object> objects = new ArrayList<>();
				objects.add(new PlayerSender(commandSender));
				objects.add("/tokens remove <player name> <tokens amount>");
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", objects));
			}
		}
	}
}
