package net.thirdshift.tokens.commands;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.redeem.factions;
import net.thirdshift.tokens.commands.redeem.key;
import net.thirdshift.tokens.commands.redeem.mcmmo;
import net.thirdshift.tokens.commands.redeem.vault;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandRedeem implements CommandExecutor {
    private Tokens plugin;

    public CommandRedeem(Tokens instance){
        this.plugin=instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player && commandSender.hasPermission("tokens.redeem")){
            if( plugin.hasCombatLogX && plugin.combatLogXEnabled && CombatUtil.isInCombat((Player) commandSender) ){
                commandSender.sendMessage("You can't use Tokens while in combat!");
                return true;
            }
            if (args.length == 0) {
                commandSender.sendMessage("Command usage: /redeem <" + plugin.getAddons() + ">");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("mcmmo")) {
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem mcmmo <skill name> <tokens to spend>");
                } else if (args[0].equalsIgnoreCase("factions")) {
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem faction <tokens to spend>");
                } else if (args[0].equalsIgnoreCase("money")) {
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem money <tokens to spend>");
                }else if(args[0].equalsIgnoreCase("key") || args[0].equalsIgnoreCase("keys")){
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem key <key>");
                }else {
                    commandSender.sendMessage(ChatColor.RED+"Invalid redeemable: " + Arrays.toString(args));
                }
            } else if (args[0].equalsIgnoreCase("mcmmo")) {
                if (args.length == 3) {
                    mcmmo.redeemMCMMO((Player) commandSender, args[1], Integer.parseInt(args[2]), plugin);
                } else {
                    commandSender.sendMessage(ChatColor.RED+"Invalid command use.");
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem mcmmo <skill name> <tokens to spend>");
                }
            }else if(args[0].equalsIgnoreCase("factions") || args[0].equalsIgnoreCase("faction")){
                if (args.length == 2) {
                    factions.redeemFactions((Player) commandSender, Integer.parseInt(args[1]), plugin);
                } else {
                    commandSender.sendMessage("/redeem factions <token amount>");
                }
            }else if(args[0].equalsIgnoreCase("money") || args[0].equalsIgnoreCase("cash")){
                if(args.length==2){
                    vault.redeemVault((Player) commandSender, Integer.parseInt(args[1]), plugin);
                }else{
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem money <tokens to spend>");
                }
            }else if(args[0].equalsIgnoreCase("key") || args[0].equalsIgnoreCase("keys") || args[0].equalsIgnoreCase("k")){
                if(args.length==2){
                    key.redeemKey((Player) commandSender, args[1]);
                }else{
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem key <key>");
                }
            }
            return true;
        }else {
            return false;
        }
    }
}
