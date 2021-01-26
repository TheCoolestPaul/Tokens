package net.thirdshift.tokens.commands.redeem;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.redeem.redeemcommands.RedeemModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabRedeem implements TabCompleter {

    final Tokens plugin;
    final RedeemCommandExecutor redeemCommandExecutor;

    public TabRedeem(Tokens instance){
        this.plugin=instance;
        redeemCommandExecutor = instance.getRedeemCommandExecutor();
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>(); // All possible completions
        List<String> ret = new ArrayList<>();// Ret the closest of all of them

        if(args.length==1){
            for(RedeemModule redeemModule :  redeemCommandExecutor.getRedeemModules().values()){
                completions.add(redeemModule.getCommand());
            }
            StringUtil.copyPartialMatches(args[0], completions, ret);
        }else if(args.length==2){
            if(args[0].equalsIgnoreCase("mcmmo") && plugin.getTokensConfigHandler().isRunningMCMMO()){
                List<String> skillList = PrimarySkillType.SKILL_NAMES;
                completions.addAll(skillList);
            }
            StringUtil.copyPartialMatches(args[1], completions, ret);
        }

        return ret;
    }
}
