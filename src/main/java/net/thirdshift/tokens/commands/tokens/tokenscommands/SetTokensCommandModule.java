package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import net.thirdshift.tokens.messages.messageData.PlayerTarget;
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
				List<Object> objects = new ArrayList<>();
				objects.add(num);
				objects.add(new PlayerSender(commandSender));
				objects.add(new PlayerTarget(target));
				if (!plugin.messageHandler.useMessage("tokens.set.sender", objects).isEmpty())
					commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.set.sender", objects));
				if (!plugin.messageHandler.useMessage("tokens.set.receiver", objects).isEmpty())
					target.sendMessage(plugin.messageHandler.useMessage("tokens.set.receiver", objects));
			} else {
				if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
					List<Object> objects = new ArrayList<>();
					objects.add(new PlayerSender(commandSender));
					objects.add(new PlayerTarget(args[0]));
					commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
				}
			}
		} else {
			if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.message").isEmpty()){
				List<Object> objects = new ArrayList<>();
				objects.add(new PlayerSender(commandSender));
				objects.add(this.getCommandUsage());
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.message", objects));
			}
			if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.correction").isEmpty()){
				List<Object> objects = new ArrayList<>();
				objects.add(new PlayerSender(commandSender));
				objects.add(this.getCommandUsage());
				commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", objects));
			}
		}
	}
}
