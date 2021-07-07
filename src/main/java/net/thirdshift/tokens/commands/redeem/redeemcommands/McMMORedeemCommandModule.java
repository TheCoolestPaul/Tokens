package net.thirdshift.tokens.commands.redeem.redeemcommands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageComponents.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class McMMORedeemCommandModule extends CommandModule {

	private final SkillTools skillTools;

	public McMMORedeemCommandModule(final TokensCustomCommandExecutor executor) {
		super(executor);
		mcMMO mcmmo = (mcMMO) Bukkit.getPluginManager().getPlugin("mcMMO");
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
		List<MessageComponent> components = new ArrayList<>();
		if (args.length!=2){
			components.add(new SenderMessageComponent(player));
			components.add(new CommandMessageComponent(getCommandUsage()));
			player.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", components));
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

		components.add(new TokensMessageComponent(toRedeem));
		components.add(new SenderMessageComponent(player));

		if (plugin.getHandler().hasEnoughTokens(player, toRedeem)) {
			if (skillTools.matchSkill(skillName) != null) {
				PrimarySkillType skill = skillTools.matchSkill(skillName);
				if (SkillTools.isChildSkill(skill)) {
					return;
				}
				McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer(player);
				components.add(new McMMOSkillMessageComponent(skillTools.getLocalizedSkillName(skill)));
				senderMcMMO.addLevels(skill, toRedeem * plugin.getTokensConfigHandler().getTokensToMCMMOLevels());
				player.sendMessage(plugin.messageHandler.useMessage("redeem.mcmmo.redeemed", components));
				plugin.getHandler().removeTokens(player, toRedeem);
			} else {
				components.add(new McMMOSkillListMessageComponent(skillTools, PrimarySkillType.values()));
				player.sendMessage(plugin.messageHandler.useMessage("redeem.mcmmo.invalid-skill", components));
			}
		} else {
			player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", components));
		}
	}
}
