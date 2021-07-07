package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageComponents.MessageComponent;
import net.thirdshift.tokens.messages.messageComponents.MoneyMessageComponent;
import net.thirdshift.tokens.messages.messageComponents.SenderMessageComponent;
import net.thirdshift.tokens.messages.messageComponents.TokensMessageComponent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuyTokensCommandModule extends CommandModule {

	public BuyTokensCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
	}

	@Override
	public String getPermission() {
		return "tokens.buy";
	}

	@Override
	public String getDescription() {
		return "Buy Tokens for money.";
	}

	@Override
	public String getCommand() {
		return "buy";
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
				List<MessageComponent> components = new ArrayList<>();
				components.add(new MoneyMessageComponent(total));
				components.add(new TokensMessageComponent(toRedeem));
				components.add(new SenderMessageComponent(commandSender));
				if (total <= plyMoney) {
					EconomyResponse r = plugin.getEconomy().withdrawPlayer((OfflinePlayer) commandSender, total);
					if (r.transactionSuccess()) {
						commandSender.sendMessage(plugin.messageHandler.useMessage("redeem.vault.buy", components));
						plugin.getHandler().addTokens((Player) commandSender, toRedeem);
					} else {
						commandSender.sendMessage(ChatColor.RED+"There was an error withdrawing money from your account");
						commandSender.sendMessage(ChatColor.RED+"The error was "+r.errorMessage);
					}
				} else {
					commandSender.sendMessage(plugin.messageHandler.useMessage("redeem.errors.no-money", components));
				}
			}else{
				commandSender.sendMessage(ChatColor.RED+"Invalid command use! Your arguments were "+ Arrays.toString(args));
				commandSender.sendMessage(ChatColor.GRAY+"Command usage: /tokens buy <tokens amount>");
			}
		}
	}
}
