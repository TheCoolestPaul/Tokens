package net.thirdshift.tokens.commands.redeem.redeemcommands;

import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VaultRedeemModule extends RedeemModule {

	@Override
	public String getCommand() {
		return "money";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"vault", "cash"};
	}

	@Override
	public String getCommandUsage() {
		return "/redeem money <tokens to redeem>";
	}

	@Override
	public void redeem(Player player, ArrayList<String> args) {
		if (args.size()!=1){
			List<Object> objects = new ArrayList<>();
			objects.add(new PlayerSender(player));
			objects.add(getCommandUsage());
			player.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
			return;
		}

		int toRedeem = Integer.parseInt(args.get(0));
		double money = plugin.getTokensConfigHandler().getVaultSellPrice()*toRedeem;
		List<Object> objects = new ArrayList<>();

		objects.add(toRedeem);
		objects.add(new PlayerSender(player));
		objects.add(money);

		if (plugin.getHandler().hasTokens(player, toRedeem)){
			EconomyResponse r = plugin.getEconomy().depositPlayer(player, money);
			if (r.transactionSuccess()) {
				plugin.getHandler().setTokens(player, plugin.getHandler().getTokens(player) - toRedeem);
				player.sendMessage(plugin.messageHandler.useMessage("redeem.vault.sell", objects));
			} else {
				player.sendMessage(String.format("An error occurred: %s", r.errorMessage));
			}
		} else {
			player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", objects));
		}

	}

	// TODO: Deal with this method properly.
	public static void purchaseVault(Player player, int toRedeem, Tokens plugin){
		double price = plugin.getTokensConfigHandler().getVaultBuyPrice();
		double plyMoney = plugin.getEconomy().getBalance(player);
		double total = (double)toRedeem * price;
		List<Object> objects = new ArrayList<>();
		objects.add(total);
		objects.add(toRedeem);
		objects.add(new PlayerSender(player));
		if (total <= plyMoney) {
			EconomyResponse r = plugin.getEconomy().withdrawPlayer(player, total);
			if (r.transactionSuccess()) {
				player.sendMessage(plugin.messageHandler.useMessage("redeem.vault.buy", objects));
				plugin.getHandler().addTokens(player, toRedeem);
			} else {
				player.sendMessage(ChatColor.RED+"There was an error withdrawing money from your account");
			}
		} else {
			player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.no-money", objects));
		}
	}
}
