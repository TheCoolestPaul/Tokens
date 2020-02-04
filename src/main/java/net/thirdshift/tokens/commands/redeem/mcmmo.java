package net.thirdshift.tokens.commands.redeem;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.EventUtils;
import net.thirdshift.tokens.Tokens;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class mcmmo {
    public static void redeemMCMMO(Player player, String skillname, int toRedeem, Tokens plugin){
        if (plugin.handler.getTokens(player)>= toRedeem) {
            if (PrimarySkillType.getSkill(skillname) != null) {
                if (plugin.handler.getTokens(player) >= toRedeem) {
                    PrimarySkillType skill = PrimarySkillType.getSkill(skillname);
                    McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer(player);
                    senderMcMMO.addLevels(skill, toRedeem * plugin.tokensToMCMMOLevels);
                    player.sendMessage("You successfully redeemed " + ChatColor.GOLD + "" + toRedeem + "" + ChatColor.WHITE + " token(s) to the mcMMO skill " + ChatColor.GRAY + "" + skill.getName());
                    plugin.handler.setTokens(player, plugin.handler.getTokens(player) - toRedeem);
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough tokens for that");
                }
            } else {
                List<String> skillList = PrimarySkillType.SKILL_NAMES;
                player.sendMessage(ChatColor.RED + "Invalid skill McMMO skill");
                player.sendMessage(ChatColor.RED + "Skills available are " + ChatColor.GRAY + "" + skillList);
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't that many tokens to redeem!");
        }
    }
}
