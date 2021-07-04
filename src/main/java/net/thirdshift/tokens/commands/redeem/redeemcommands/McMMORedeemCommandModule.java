package net.thirdshift.tokens.commands.redeem.redeemcommands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class McMMORedeemCommandModule extends CommandModule {

	private final mcMMO mcmmo;
	private final SkillTools skillTools;

	public McMMORedeemCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
		this.mcmmo = (mcMMO) Bukkit.getPluginManager().getPlugin("mcMMO");
		assert mcmmo != null;
		this.skillTools = mcmmo.getSkillTools();
	}

	@Override
	public String getPermission() {
		return "tokens.redeem.mcmmo";
	}

	@Override
	public String getDescription() {
		return "Gives you a level in a skill";
	}

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
	public void onCommand(CommandSender commandSender, final String[] args) {
		if ( !(commandSender instanceof Player) || !commandSender.hasPermission("tokens.redeem.mcmmo"))
			return;

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
			if (skillTools.matchSkill(skillName) != null) {
				PrimarySkillType skill = skillTools.matchSkill(skillName);
				if (SkillTools.isChildSkill(skill)) {
					return;
				}
				McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer(player);
				objects.add(skill);
				senderMcMMO.addLevels(skill, toRedeem * plugin.getTokensConfigHandler().getTokensToMCMMOLevels());
				player.sendMessage(plugin.messageHandler.useMessage("redeem.mcmmo.redeemed", objects));
				plugin.getHandler().removeTokens(player, toRedeem);
			} else {
				PrimarySkillType[] skillList = PrimarySkillType.values();
				objects.add(skillList);
				player.sendMessage(plugin.messageHandler.useMessage("redeem.mcmmo.invalid-skill", objects));
			}
		} else {
			player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", objects));
		}
	}
}
