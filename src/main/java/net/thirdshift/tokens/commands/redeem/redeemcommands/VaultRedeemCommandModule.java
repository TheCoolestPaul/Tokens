package net.thirdshift.tokens.commands.redeem.redeemcommands;

import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VaultRedeemCommandModule extends CommandModule {

	public VaultRedeemCommandModule() {
		super();
		this.command = "money";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"vault", "cash", "eco"};
	}

	@Override
	public String getCommandUsage() {
		return "/redeem money <tokens to redeem>";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;
		List<Object> objects = new ArrayList<>();
		if (args.length!=1){
			objects.add(new PlayerSender(player));
			objects.add(getCommandUsage());
			player.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", objects));
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

		objects.add(toRedeem);
		objects.add(new PlayerSender(player));
		objects.add(money);

		if (plugin.getHandler().hasEnoughTokens(player, toRedeem)){
			EconomyResponse r = plugin.getEconomy().depositPlayer(player, money);
			if (r.transactionSuccess()) {
				plugin.getHandler().removeTokens(player, toRedeem);
				player.sendMessage(plugin.messageHandler.useMessage("redeem.vault.sell", objects));
			} else {
				player.sendMessage(String.format("An error occurred: %s", r.errorMessage));
			}
		} else {
			player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", objects));
		}

	}
}
