package net.thirdshift.tokens.commands.redeem;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.redeem.redeemcommands.RedeemModule;
import net.thirdshift.tokens.commands.redeem.redeemcommands.key;
import net.thirdshift.tokens.commands.redeem.redeemcommands.mcmmo;
import net.thirdshift.tokens.commands.redeem.redeemcommands.vault;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RedeemCommandExecutor implements CommandExecutor {
    private final Tokens plugin;
    private final HashMap<String, RedeemModule> commandRedeemMap;

    public RedeemCommandExecutor(final Tokens plugin){
        this.plugin=plugin;
        commandRedeemMap = new HashMap<>();
    }

    public void registerRedeemModule(final RedeemModule redeemModule){
        if (commandRedeemMap.get(redeemModule.getCommand())!=null){
            plugin.getLogger().warning("A module tried to load with command "+redeemModule.getCommand());
        } else {
            commandRedeemMap.put(redeemModule.getCommand(), redeemModule);
            plugin.getLogger().info("Added module "+ redeemModule.getCommand());
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player && commandSender.hasPermission("tokens.redeem")){
            if( plugin.hasCombatLogX && plugin.combatLogXEnabled && CombatUtil.isInCombat((Player) commandSender) ){
                if(!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
                    List<Object> objects = new ArrayList<>();
                    objects.add(new PlayerSender((Player) commandSender));
                    commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", objects));
                }
                return true;
            }
            if (args.length == 0) {
                commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem <" + plugin.getAddons() + ">");
            } else if (args.length == 1) {
                if (plugin.mcmmoEnabled && plugin.hasMCMMO && args[0].equalsIgnoreCase("mcmmo")) {
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem mcmmo <skill name> <tokens to spend>");
                } else if (plugin.hasFactions && plugin.factionsEnabled && args[0].equalsIgnoreCase("factions") || args[0].equalsIgnoreCase("faction") || args[0].equalsIgnoreCase("f")) {
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem faction <tokens to spend>");
                } else if (plugin.hasVault && plugin.vaultEnabled && args[0].equalsIgnoreCase("money") || args[0].equalsIgnoreCase("cash")) {
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem money <tokens to spend>");
                }else if(args[0].equalsIgnoreCase("key") || args[0].equalsIgnoreCase("keys") || args[0].equalsIgnoreCase("k")){
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem key <key>");
                }else {
                    commandSender.sendMessage(ChatColor.RED+"Invalid redeemable: " + Arrays.toString(args));
                }
            }else {
                for(RedeemModule redeemModule : commandRedeemMap.values()){
                    if (args[0].equalsIgnoreCase(redeemModule.getCommand())){
                        redeemModule.redeem((Player) commandSender, Integer.parseInt(args[1]));
                        return true;
                    }
                    for(String alias : redeemModule.getCommandAliases()){
                        if(args[0].equalsIgnoreCase(alias)){
                            redeemModule.redeem((Player) commandSender, Integer.parseInt(args[1]));
                            return true;
                        }
                    }
                }
            }/* else if (plugin.mcmmoEnabled && plugin.hasMCMMO && args[0].equalsIgnoreCase("mcmmo")) {
                if (args.length == 3) {
                    mcmmo.redeemMCMMO((Player) commandSender, args[1], Integer.parseInt(args[2]), plugin);
                } else {
                    List<Object> objects = new ArrayList<>();
                    objects.add(new PlayerSender((Player)commandSender));
                    objects.add("/redeem mcmmo <skill name> <tokens to spend>");
                    commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
                }
            }else if(plugin.hasFactions && plugin.factionsEnabled && args[0].equalsIgnoreCase("factions") || args[0].equalsIgnoreCase("faction") || args[0].equalsIgnoreCase("f")){
               if (args.length == 2) {
                   FactionCommandRedeem.redeemFactions((Player) commandSender, Integer.parseInt(args[1]), plugin);
               } else {
                   commandSender.sendMessage("/redeem factions <token amount>");
               }
            }
            else if(plugin.hasVault && plugin.vaultEnabled && args[0].equalsIgnoreCase("money") || args[0].equalsIgnoreCase("cash")){
                if(args.length==2){
                    vault.redeemVault((Player) commandSender, Integer.parseInt(args[1]), plugin);
                }else{
                    List<Object> objects = new ArrayList<>();
                    objects.add(new PlayerSender((Player)commandSender));
                    objects.add("/redeem money <tokens to spend>");
                    commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
                }
                return true;
            }else if(args[0].equalsIgnoreCase("key") || args[0].equalsIgnoreCase("keys") || args[0].equalsIgnoreCase("k")){
                if(args.length==2){
                    key.redeemKey((Player) commandSender, args[1], plugin);
                }else{
                    List<Object> objects = new ArrayList<>();
                    objects.add(new PlayerSender((Player)commandSender));
                    objects.add("/redeem key <key>");
                    commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
                }
            }
            */
            return false;
        }else {
            return false;
        }
    }
}
