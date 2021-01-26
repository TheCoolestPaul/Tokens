package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.messages.messageData.PlayerSender;
import net.thirdshift.tokens.messages.messageData.PlayerTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AddTokensModule extends TokensModule {

	@Override
	public String getCommand() {
		return "add";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[0];
	}

	@Override
	public String getCommandUsage() {
		return "/tokens add <player name> <tokens to add>";
	}

	@Override
	public void onCommand(final CommandSender commandSender, final String[] args) {
		if (commandSender instanceof Player){ // Console reigns supreme.
			if (!commandSender.hasPermission("tokens.add"))
				return;
		}

		if (args.length == 3) {
			Player target = Bukkit.getPlayer(args[1]);
			if (target!=null){
				try {
					int num = Integer.parseInt(args[2]);
					tokensHandler.addTokens(target, num);
					if(!plugin.messageHandler.getMessage("tokens.add.sender").isEmpty()){
						List<Object> objects = new ArrayList<>();
						objects.add(new PlayerSender(commandSender));
						objects.add(num);
						objects.add(new PlayerTarget(target));
						commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.add.sender", objects));
					}
					if(!plugin.messageHandler.getMessage("tokens.add.receiver").isEmpty()){
						List<Object> objects = new ArrayList<>();
						objects.add(new PlayerSender(commandSender));
						objects.add(num);
						objects.add(new PlayerTarget(target));
						target.sendMessage(plugin.messageHandler.useMessage("tokens.add.receiver", objects));
					}
				} catch (NumberFormatException e){
					commandSender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
				}
			} else {
				if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
					List<Object> objects = new ArrayList<>();
					objects.add(new PlayerSender(commandSender));
					objects.add(new PlayerTarget(args[1]));
					commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
				}
			}
		} else {
			if(!plugin.messageHandler.getMessage("tokens.errors.invalid.commandcorrection").isEmpty()){
				List<Object> objects = new ArrayList<>();
				objects.add(new PlayerSender(commandSender));
				objects.add(this.getCommandUsage());
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", objects));
			}
		}

	}
}