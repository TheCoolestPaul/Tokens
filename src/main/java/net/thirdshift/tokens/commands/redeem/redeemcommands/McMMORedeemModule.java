package net.thirdshift.tokens.commands.redeem.redeemcommands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.EventUtils;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class McMMORedeemModule extends RedeemModule {

	@Override
	public String getCommand() {
		return "mcmmo";
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
	public void redeem(Player player, final ArrayList<String> args) {
		if(args.size()!=2){
			List<Object> objects = new ArrayList<>();
			objects.add(new PlayerSender(player));
			objects.add(getCommandUsage());
			player.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
			return;
		}

		String skillName = args.get(0);
		int toRedeem = Integer.parseInt(args.get(1));
		List<Object> objects = new ArrayList<>();

		objects.add(toRedeem);
		objects.add(player);

		if (plugin.getHandler().hasTokens(player, toRedeem)) {
			if (PrimarySkillType.getSkill(skillName) != null) {
				if (plugin.getHandler().getTokens(player) >= toRedeem) {
					PrimarySkillType skill = PrimarySkillType.getSkill(skillName);
					McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer(player);
					objects.add(skill);
					senderMcMMO.addLevels(skill, toRedeem * plugin.tokensToMCMMOLevels);
					player.sendMessage(plugin.messageHandler.useMessage("redeem.mcmmo.redeemed", objects));
					plugin.getHandler().setTokens(player, plugin.getHandler().getTokens(player) - toRedeem);
				} else {
					player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", objects));
				}
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
