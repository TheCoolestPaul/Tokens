package net.thirdshift.commands;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.EventUtils;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
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
                }
                if (args[0] != null && (args[0].equalsIgnoreCase("factions") || args[0].equalsIgnoreCase("faction"))) {
                    if(args.length == 2){
                        int tokeToPower = plugin.tokenToFactionPower;
                        int toRedeem = Integer.parseInt(args[1]);
                        if(toRedeem <= plugin.getTokens((Player) sender)) {
                            FPlayer facPly = FPlayers.getInstance().getByPlayer(senderPly);
                            if (facPly != null) {
                                facPly.setPowerBoost(facPly.getPowerBoost()+(toRedeem*tokeToPower));
                                plugin.setTokens((Player)sender, (plugin.getTokens((Player)sender) - toRedeem));
                                sender.sendMessage("You redeemed "+ChatColor.GOLD+""+toRedeem+" token(s)");
                                sender.sendMessage("Your maximum faction power is now "+ChatColor.GREEN+""+ facPly.getPowerMax());
                                return true;
                            }
                            return true;
                        }else{
                            sender.sendMessage(ChatColor.RED + "You don't that many tokens to redeem!");
                            return true;
                        }
                    }else{
                        sender.sendMessage("/redeem factions <token amount>");
                        return true;
                    }
                } else if (args[0] != null && args[0].equalsIgnoreCase("mcmmo")) {
                    if (args.length == 3) {
                        PrimarySkillType skill;
                        int toRedeem = Integer.parseInt(args[2]);
                        if (plugin.getTokens((Player)sender) >= toRedeem) {
                            if ((PrimarySkillType.getSkill(args[1])) != null) {
                                if (plugin.getTokens((Player)sender) >= Integer.parseInt(args[2])) {
                                    skill = PrimarySkillType.getSkill(args[1]);
                                    McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer((Entity) sender);
                                    senderMcMMO.addLevels(skill, toRedeem*plugin.tokensToMCMMOLevels);
                                    sender.sendMessage("You successfully redeemed " + ChatColor.GOLD + "" + toRedeem + "" + ChatColor.WHITE + " token(s) to the mcMMO skill " + ChatColor.GRAY + "" + skill.getName());
                                    plugin.setTokens((Player)sender, (plugin.getTokens((Player)sender)-toRedeem));
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
                        sender.sendMessage(ChatColor.RED + "/redeem mcmmo <skill name> <token amount");
                        return true;
                    }
                } else if(args[0] !=null &&  (args[0].equalsIgnoreCase("cash") || args[0].equalsIgnoreCase("money") ) && plugin.vaultEnabled && plugin.vaultSell) {
                    if(args.length == 2 && args[1] != null){
                        int toRedeem = Integer.parseInt(args[1]);
                        if(toRedeem <= plugin.getTokens((Player)sender)) {
                            EconomyResponse r = Tokens.economy.depositPlayer(senderPly, plugin.vaultSellPrice * toRedeem);
                            if (r.transactionSuccess()) {
                                sender.sendMessage(String.format("You have successfully redeemed " + ChatColor.GOLD + "" + toRedeem + "" + ChatColor.WHITE + " token(s) for %s", Tokens.economy.format(r.amount)));
                                plugin.setTokens((Player)sender,(plugin.getTokens((Player)sender)-toRedeem));
                                return true;
                            } else {
                                sender.sendMessage(String.format("An error occurred: %s", r.errorMessage));
                                return true;
                            }
                        }else{
                            sender.sendMessage(ChatColor.RED + "You don't that many tokens to redeem!");
                            return true;
                        }
                    }else{
                        sender.sendMessage(ChatColor.RED+"/redeem <money> <token amount>");
                        return true;
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
    }

    private String whatHooks() {
        String response = "< ";
        if ( plugin.hasFactions && plugin.factionsEnabled) {
            response = response+"| factions ";
        }
        if (plugin.hasMCMMO && plugin.mcmmoEnabled) {
            response = response+"| mcmmo ";
        }
        if(plugin.hasVault && plugin.vaultEnabled && plugin.vaultSell){
            response = response+"| cash ";
        }
        response=response+"|>";
        return response;
    }

}
