package net.thirdshift.commands;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.EventUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thirdshift.Tokens;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class CMDRedeem implements CommandExecutor {
    private Tokens plugin;
    public CMDRedeem(Tokens instance){
        plugin = instance;
    }
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player senderPly = (Player) sender;
        if (sender != null) {
            if (args.length != 0) {
                if (plugin.combatLogXBlockTokens && plugin.combatLogXEnabled) {
                    if(CombatUtil.isInCombat(senderPly)) {
                        sender.sendMessage(ChatColor.RED+"You can't use tokens while in combat!");
                        return true;
                    }
                    return true;
                }
                if (args[0] != null && (args[0].equalsIgnoreCase("factions") || args[0].equalsIgnoreCase("faction"))) {
                    //do faction shit
                    return true;
                } else if (args[0] != null && args[0].equalsIgnoreCase("mcmmo")) {
                    if (args.length == 3) {
                        PrimarySkillType skill;
                        int toRedeem = Integer.parseInt(args[2]);
                        if (plugin.getRDatabase().getTokens(((Player) sender).getUniqueId()) >= toRedeem) {
                            if ((PrimarySkillType.getSkill(args[1])) != null) {
                                if (plugin.getRDatabase().getTokens(((Player) sender).getUniqueId()) >= Integer.parseInt(args[2])) {
                                    skill = PrimarySkillType.getSkill(args[1]);
                                    McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer((Entity) sender);
                                    senderMcMMO.addLevels(skill, toRedeem);
                                    sender.sendMessage("You successfully redeemed " + ChatColor.GOLD + "" + toRedeem + "" + ChatColor.WHITE + " token(s) to the mcMMO skill " + ChatColor.GRAY + "" + skill.getName());
                                    plugin.getRDatabase().setTokens(((Player) sender).getUniqueId(), plugin.getRDatabase().getTokens(((Player) sender).getUniqueId()) - toRedeem);
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.RED + "You don't have enough tokens for that");
                                    return true;
                                }
                            } else {
                                List<String> skillList = PrimarySkillType.SKILL_NAMES;
                                sender.sendMessage(ChatColor.RED + "Invalid skill McMMO skill");
                                sender.sendMessage(ChatColor.RED + "Skills available are " + ChatColor.GRAY + "" + skillList);
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You don't that many tokens to redeem!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid command use");
                        sender.sendMessage(ChatColor.RED + "/redeem mcmmo <skill name> <token amount");
                        return true;
                    }
                } else if(args[0] !=null &&  (args[0].equalsIgnoreCase("cash") || args[0].equalsIgnoreCase("money") ) && plugin.vaultEnabled && plugin.vaultSell) {
                    sender.sendMessage(String.format("You have %s", Tokens.economy.format(Tokens.economy.getBalance(senderPly))));
                    EconomyResponse r = Tokens.economy.depositPlayer(senderPly, 10.00 );
                    if(r.transactionSuccess()) {
                        sender.sendMessage(String.format("You were given %s and now have %s", Tokens.economy.format(r.amount), Tokens.economy.format(r.balance)));
                    } else {
                        sender.sendMessage(String.format("An error occurred: %s", r.errorMessage));
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "/redeem " + whatHooks());
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "/redeem " + whatHooks());
                return true;
            }
        }else{
            plugin.getLogger().info("Console issued the /redeem command");
            return true;
        }
        return true;
    }

    private String whatHooks() {
        String response = "";
        if (plugin.hasFactions && plugin.hasMCMMO && plugin.hasVault) {
            response = "<mcmmo | money | factions>";
        } else if (plugin.hasFactions) {
            response = "factions";
        } else if (plugin.hasMCMMO) {
            response = "mcmmo";
        }else if(plugin.hasVault){
            response = "cash";
        }else{
            response="ERROR";
        }
        return response;
    }

}
