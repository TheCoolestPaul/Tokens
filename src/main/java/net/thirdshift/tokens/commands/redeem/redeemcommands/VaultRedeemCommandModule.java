package net.thirdshift.tokens.commands.redeem.redeemcommands;

import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageComponents.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VaultRedeemCommandModule extends CommandModule {

	public VaultRedeemCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
	}

	@Override
	public String getPermission() {
		return "tokens.redeem.sell";
	}

	@Override
	public String getDescription() {
		return "Turns your Tokens into money";
	}

	@Override
	public String getCommand() {
		return "money";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"sell, vault", "cash", "eco"};
	}

	@Override
	public String getCommandUsage() {
		return "/redeem money <tokens to redeem>";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {
		if ( !(commandSender instanceof Player) || !commandSender.hasPermission("tokens.redeem.sell"))
			return;

		Player player = (Player) commandSender;
		List<MessageComponent> components = new ArrayList<>();
		if (args.length!=1){
			components.add(new SenderMessageComponent(player));
			components.add(new CommandMessageComponent(getCommandUsage()));
			player.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", components));
			return;
		}

		int toRedeem;
		try {
			toRedeem = Integer.parseInt(args[0]);
		} catch(NumberFormatException e){
			player.sendMessage(ChatColor.RED +"Invalid command, " + args[0] + " is not a number!");
			return;
		}

		double money = plugin.getTokensConfigHandler().getVaultSellPrice()*toRedeem;

		components.add(new TokensMessageComponent(toRedeem));
		components.add(new SenderMessageComponent(player));
		components.add(new MoneyMessageComponent(money));

		if (plugin.getHandler().hasEnoughTokens(player, toRedeem)){
			EconomyResponse r = plugin.getEconomy().depositPlayer(player, money);
			if (r.transactionSuccess()) {
				plugin.getHandler().removeTokens(player, toRedeem);
				player.sendMessage(plugin.messageHandler.useMessage("redeem.vault.sell", components));
			} else {
				player.sendMessage(String.format("An error occurred: %s", r.errorMessage));
			}
		} else {
			player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", components));
		}

	}
}
