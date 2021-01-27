package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuyTokensCommandModule extends CommandModule {

	public BuyTokensCommandModule() {
		this.command="buy";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"b"};
	}

	@Override
	public String getCommandUsage() {
		return "/tokens buy <amount to buy>";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {
		if(commandSender instanceof Player && commandSender.hasPermission("tokens.buy")){
			if(args.length==1){
				int toRedeem;
				try {
					toRedeem = Integer.parseInt(args[0]);
				} catch (NumberFormatException e){
					commandSender.sendMessage(ChatColor.RED+"Invalid command use!");
					commandSender.sendMessage(getCommandUsage());
					return;
				}
				double price = plugin.getTokensConfigHandler().getVaultBuyPrice();
				double plyMoney = plugin.getEconomy().getBalance((OfflinePlayer) commandSender);
				double total = (double)toRedeem * price;
				List<Object> objects = new ArrayList<>();
				objects.add(total);
				objects.add(toRedeem);
				objects.add(new PlayerSender(commandSender));
				if (total <= plyMoney) {
					EconomyResponse r = plugin.getEconomy().withdrawPlayer((OfflinePlayer) commandSender, total);
					if (r.transactionSuccess()) {
						commandSender.sendMessage(plugin.messageHandler.useMessage("redeem.vault.buy", objects));
						plugin.getHandler().addTokens((Player) commandSender, toRedeem);
					} else {
						commandSender.sendMessage(ChatColor.RED+"There was an error withdrawing money from your account");
						commandSender.sendMessage(ChatColor.RED+"The error was "+r.errorMessage);
					}
				} else {
					commandSender.sendMessage(plugin.messageHandler.useMessage("redeem.errors.no-money", objects));
				}
			}else{
				commandSender.sendMessage(ChatColor.RED+"Invalid command use! Your arguments were "+ Arrays.toString(args));
				commandSender.sendMessage(ChatColor.GRAY+"Command usage: /tokens buy <tokens amount>");
			}
		}
	}
}
