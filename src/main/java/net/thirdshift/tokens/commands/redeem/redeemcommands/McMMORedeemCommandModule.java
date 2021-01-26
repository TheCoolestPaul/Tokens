package net.thirdshift.tokens.commands.redeem.redeemcommands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.EventUtils;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class McMMORedeemCommandModule extends CommandModule {

	public McMMORedeemCommandModule() {
		super();
		this.command = "mcmmo";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[0];
	}

	@Override
	public String getCommandUsage() {
		return "/redeem mcmmo <skill name> <tokens to spend>";
	}

	@Override
	public void onCommand(CommandSender commandSender, final String[] args) {
		Player player = (Player) commandSender;
		List<Object> objects = new ArrayList<>();
		if (args.length!=2){
			objects.add(new PlayerSender(player));
			objects.add(getCommandUsage());
			player.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", objects));
			return;
		}

		String skillName = args[0];
		int toRedeem;
		try {
			toRedeem = Integer.parseInt(args[1]);
		}catch(NumberFormatException e){
			player.sendMessage(ChatColor.RED +"Invalid command, "+args[1]+" is not a number!");
			return;
		}

		objects.add(toRedeem);
		objects.add(player);

		if (plugin.getHandler().hasEnoughTokens(player, toRedeem)) {
			if (PrimarySkillType.getSkill(skillName) != null) {
				PrimarySkillType skill = PrimarySkillType.getSkill(skillName);
				McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer(player);
				objects.add(skill);
				senderMcMMO.addLevels(skill, toRedeem * plugin.getTokensConfigHandler().getTokensToMCMMOLevels());
				player.sendMessage(plugin.messageHandler.useMessage("redeem.mcmmo.redeemed", objects));
				plugin.getHandler().removeTokens(player, toRedeem);
			} else {
				List<String> skillList = PrimarySkillType.SKILL_NAMES;
				objects.add(skillList);
				player.sendMessage(plugin.messageHandler.useMessage("redeem.mcmmo.invalid-skill", objects));
			}
		} else {
			player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", objects));
		}
	}
}
