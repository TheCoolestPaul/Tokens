package net.thirdshift.tokens.commands;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.thirdshift.tokens.Tokens;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TabRedeem implements TabCompleter {

    Tokens plugin;

    public TabRedeem(Tokens instance){
        this.plugin=instance;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>(); // All possible completions
        List<String> ret = new ArrayList<>();// Ret the closest of all of them

        if(args.length==1){
            if(plugin.mcmmoEnabled)
                completions.add("mcmmo");
            if(plugin.factionsEnabled)
                completions.add("factions");
            if(plugin.vaultEnabled && plugin.vaultSell)
                completions.add("money");
            StringUtil.copyPartialMatches(args[0], completions, ret);
        }else if(args.length==2){
            if(args[0].equalsIgnoreCase("mcmmo") && plugin.mcmmoEnabled){
                List<String> skillList = PrimarySkillType.SKILL_NAMES;
                completions.addAll(skillList);
            }
            StringUtil.copyPartialMatches(args[1], completions, ret);
        }

        return ret;
    }
}
