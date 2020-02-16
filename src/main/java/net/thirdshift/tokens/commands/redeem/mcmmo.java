package net.thirdshift.tokens.commands.redeem;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.EventUtils;
import net.thirdshift.tokens.Tokens;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class mcmmo {
    public static void redeemMCMMO(Player player, String skillname, int toRedeem, Tokens plugin){
        List<Object> objects = new ArrayList<>();
        objects.add(toRedeem);
        objects.add(player);
        if (plugin.handler.getTokens(player)>= toRedeem) {
            if (PrimarySkillType.getSkill(skillname) != null) {
                if (plugin.handler.getTokens(player) >= toRedeem) {
                    PrimarySkillType skill = PrimarySkillType.getSkill(skillname);
                    McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer(player);
                    objects.add(skill);
                    senderMcMMO.addLevels(skill, toRedeem * plugin.tokensToMCMMOLevels);
                    player.sendMessage(plugin.messageHandler.useMessage("redeem.mcmmo.redeemed", objects));
                    plugin.handler.setTokens(player, plugin.handler.getTokens(player) - toRedeem);
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
