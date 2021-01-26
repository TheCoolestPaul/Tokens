package net.thirdshift.tokens.commands.redeem.redeemcommands;

import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.keys.Key;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class KeyRedeemCommandModule extends CommandModule {

	public KeyRedeemCommandModule() {
		super();
		this.command = "key";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[]{"k"};
	}

	@Override
	public String getCommandUsage() {
		return "/redeem key <key>";
	}

	@Override
	public void onCommand(CommandSender commandSender, ArrayList<String> args) {
		Player player = (Player) commandSender;
		List<Object> objects = new ArrayList<>();
		if (args.size()!=1){
			objects.add(new PlayerSender(player));
			objects.add(getCommandUsage());
			player.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", objects));
			return;
		}

		String keyName = args.get(0);

		if(plugin.keyHandler.isValidKey(keyName)){
			if(plugin.keyHandler.getKey(keyName).enabled) {
				Key key = plugin.keyHandler.getKey(keyName);
				if(key.getPlayerCooldown(player) != -1) {
					long timeLeft = System.currentTimeMillis() - key.getPlayerCooldown(player);
					if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= TimeUnit.MINUTES.toSeconds(key.getCooldown())) {
						player.sendMessage(ChatColor.GRAY + "You redeemed " + (ChatColor.GOLD) + key.getTokens() + (ChatColor.GRAY) + " Tokens");
						plugin.getHandler().addTokens(player, key.getTokens());
						if (key.oneTime) {
							key.setPlayerCooldown(player, -1);
						} else {
							key.setPlayerCooldown(player, System.currentTimeMillis());
						}
					} else {
						long actTimeLeft = (TimeUnit.MINUTES.toMillis(key.getCooldown()) - timeLeft);
						if(actTimeLeft >= 1000 && TimeUnit.MILLISECONDS.toSeconds(actTimeLeft) < 60){
							player.sendMessage(ChatColor.GRAY.toString()+"You have "+TimeUnit.MILLISECONDS.toSeconds(actTimeLeft)+" seconds before you can redeem key "+ChatColor.RED+key.keyString+ChatColor.GRAY+" again.");
						} else if (TimeUnit.MILLISECONDS.toSeconds(actTimeLeft) >= 60 && TimeUnit.MILLISECONDS.toMinutes(actTimeLeft) < 60){
							player.sendMessage(ChatColor.GRAY.toString()+"You have "+TimeUnit.MILLISECONDS.toMinutes(actTimeLeft)+" minutes and "+( TimeUnit.MILLISECONDS.toSeconds(actTimeLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(actTimeLeft)))+" seconds before you can redeem key "+ChatColor.RED+key.keyString+ChatColor.GRAY+" again.");
						}else if (TimeUnit.MILLISECONDS.toMinutes(actTimeLeft) >= 60 && TimeUnit.MILLISECONDS.toHours(actTimeLeft) < 24){
							player.sendMessage(ChatColor.GRAY.toString()+"You have "+TimeUnit.MILLISECONDS.toHours(actTimeLeft)+" hours and "+( TimeUnit.MILLISECONDS.toMinutes(actTimeLeft) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(actTimeLeft)))+" minutes before you can redeem key "+ChatColor.RED+key.keyString+ChatColor.GRAY+" again");
						}else if(TimeUnit.MILLISECONDS.toHours(actTimeLeft) >= 24 && TimeUnit.MILLISECONDS.toDays(actTimeLeft) < 365){
							player.sendMessage(ChatColor.GRAY.toString()+"You have "+TimeUnit.MILLISECONDS.toDays(actTimeLeft)+" days and "+( TimeUnit.MILLISECONDS.toHours(actTimeLeft) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(actTimeLeft)))+" hours before you can redeem key "+ChatColor.RED+key.keyString+ChatColor.GRAY+" again");
						}
					}
				}else{
					player.sendMessage(ChatColor.GRAY+"You can't redeem key "+key.keyString+" again.");
				}
			}else{
				player.sendMessage(ChatColor.RED+"Key "+keyName+" is NOT a valid key!");
			}
		}else{
			player.sendMessage(ChatColor.RED+"Key "+keyName+" is NOT a valid key!");
		}
	}
}
