package net.thirdshift.tokens.commands;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.redeem.vault;
import net.thirdshift.tokens.messages.playerTypes.PlayerSender;
import net.thirdshift.tokens.messages.playerTypes.PlayerTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandTokens implements CommandExecutor {

    private Tokens plugin;

    public CommandTokens(Tokens instance){this.plugin=instance;}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length==0){
            if(commandSender instanceof Player){
                if(plugin.messageHandler.getMessage("tokens.main").isEmpty()){
                    return false;
                }
                List<Object> objects = new ArrayList<>();
                objects.add(new PlayerSender((Player) commandSender));
                objects.add(plugin.handler.getTokens((Player) commandSender));
                commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.main", objects));
                return true;
            }else return false;
        }
        if(args[0].equalsIgnoreCase("add")) {
            if (commandSender instanceof Player) {
                if (commandSender.hasPermission("tokens.add")) {
                    if( plugin.hasCombatLogX && plugin.combatLogXEnabled && CombatUtil.isInCombat((Player) commandSender) ){
                        if(!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender((Player) commandSender));
                            commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", objects));
                        }
                        return true;
                    }
                    if (args.length == 3) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target!=null) {
                            int num = Integer.parseInt(args[2]);
                            plugin.handler.addTokens(target, num);
                            if(!plugin.messageHandler.getMessage("tokens.add.sender").isEmpty()){
                                List<Object> objects = new ArrayList<>();
                                objects.add(new PlayerSender((Player) commandSender));
                                objects.add(num);
                                objects.add(new PlayerTarget(target));
                                commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.add.sender", objects));
                            }
                            if(!plugin.messageHandler.getMessage("tokens.add.receiver").isEmpty()){
                                List<Object> objects = new ArrayList<>();
                                objects.add(new PlayerSender((Player) commandSender));
                                objects.add(num);
                                objects.add(new PlayerTarget(target));
                                target.sendMessage(plugin.messageHandler.useMessage("tokens.add.receiver", objects));
                            }
                        }else{
                            if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                                List<Object> objects = new ArrayList<>();
                                objects.add(new PlayerSender((Player)commandSender));
                                objects.add(new PlayerTarget(args[1]));
                                commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                            }
                        }
                    } else {
                        if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.message").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender((Player)commandSender));
                            objects.add("/tokens add <player name> <tokens amount>");
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.message", objects));
                        }
                        if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command-correction").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender((Player)commandSender));
                            objects.add("/tokens add <player name> <tokens amount>");
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
                        }
                    }
                }
            } else {
                Player target = Bukkit.getPlayer(args[1]);
                if(target!=null) {
                    int num = Integer.parseInt(args[2]);
                    plugin.handler.addTokens(target, num);
                    if(!plugin.messageHandler.getMessage("tokens.add.sender").isEmpty()){
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerSender(commandSender.getName()));
                        objects.add(num);
                        objects.add(new PlayerTarget(target));
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.add.sender", objects));
                    }
                }else{
                    if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerSender(commandSender.getName()));
                        objects.add(new PlayerTarget(args[1]));
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                    }
                }
            }
            return true;
        }else if(args[0].equalsIgnoreCase("set")){
            if(args.length==3){
                if(commandSender instanceof Player){
                    if(commandSender.hasPermission("tokens.set")){
                        if( plugin.hasCombatLogX && plugin.combatLogXEnabled && CombatUtil.isInCombat((Player) commandSender) ){
                            if(!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
                                List<Object> objects = new ArrayList<>();
                                objects.add(new PlayerSender((Player) commandSender));
                                commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", objects));
                            }
                            return true;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target!=null){
                            plugin.handler.setTokens(target, Integer.parseInt(args[2]));
                            commandSender.sendMessage("Set "+target.getName()+"'s tokens to "+args[2]);
                        }else{
                            commandSender.sendMessage(ChatColor.RED+"Couldn't find player "+ ChatColor.GRAY + args[1] + ChatColor.RED +". did you spell their username correct?");
                        }
                    }
                }else{
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target!=null){
                        plugin.handler.setTokens(target, Integer.parseInt(args[2]));
                        commandSender.sendMessage("Set "+target.getName()+"'s tokens to "+args[2]);
                    }else{
                        commandSender.sendMessage(ChatColor.RED+"Couldn't find player "+ ChatColor.GRAY + args[1] + ChatColor.RED +". did you spell their username correct?");
                    }
                }
            }else{
                commandSender.sendMessage(ChatColor.RED + "Invalid command use.");
                commandSender.sendMessage(ChatColor.GRAY + "Command usage: /tokens set <player name> <tokens amount>");
            }
            return true;
        }else if (args[0].equalsIgnoreCase("remove")){
            if(commandSender instanceof Player){
                if(commandSender.hasPermission("tokens.remove")){
                    if(args.length==3){
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target!=null){
                            int num = Integer.parseInt(args[2]);
                            plugin.handler.removeTokens(target, num);
                            commandSender.sendMessage(ChatColor.GRAY+"You removed "+ChatColor.GOLD+args[2]+ChatColor.GRAY+" tokens from "+args[1]);
                        }else{
                            commandSender.sendMessage(ChatColor.RED+"Couldn't find player "+ ChatColor.GRAY + args[1] + ChatColor.RED +". did you spell their username correct?");
                        }
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "Invalid command use.");
                        commandSender.sendMessage(ChatColor.GRAY + "Command usage: /tokens remove <player name> <tokens amount>");
                    }
                }else return false;
            }else{
                if(args.length==3){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target!=null){
                        int num = Integer.parseInt(args[2]);
                        plugin.handler.setTokens(target, num);
                        commandSender.sendMessage(ChatColor.GRAY+"Set "+ChatColor.GREEN+""+args[1]+""+ChatColor.GRAY+" tokens to "+ChatColor.GOLD+""+args[2]);
                    }else{
                        commandSender.sendMessage(ChatColor.RED+"Couldn't find player "+ ChatColor.GRAY + args[1] + ChatColor.RED +". did you spell their username correct?");
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED + "Invalid command use.");
                    commandSender.sendMessage(ChatColor.GRAY + "Command usage: /tokens remove <player name> <tokens amount>");
                }
            }
            return true;
        }else if(args[0].equalsIgnoreCase("give")) {
            if(commandSender instanceof Player){
                if( plugin.hasCombatLogX && plugin.combatLogXEnabled && CombatUtil.isInCombat((Player) commandSender) ){
                    if(!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerSender((Player) commandSender));
                        commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", objects));
                    }
                    return true;
                }
                if(args.length==3){
                    int num = Integer.parseInt(args[2]);
                    if(plugin.handler.getTokens((Player) commandSender) >= num) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            if(target.equals(commandSender)){
                                commandSender.sendMessage(ChatColor.RED+"You can't give tokens to yourself.");
                                return true;
                            }
                            plugin.handler.removeTokens((Player) commandSender, num);
                            plugin.handler.addTokens(target, num);
                            PlayerSender sender = new PlayerSender((Player) commandSender);
                            PlayerTarget playerTarget = new PlayerTarget(target);
                            List<Object> stuff = new ArrayList<>();
                            stuff.add(sender);
                            stuff.add(playerTarget);
                            stuff.add(num);
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.give.sender", stuff));
                            target.sendMessage(plugin.messageHandler.useMessage("tokens.give.receiver", stuff));
                        } else {
                            commandSender.sendMessage(ChatColor.RED+"Couldn't find player "+ ChatColor.GRAY + args[1] + ChatColor.RED +". did you spell their username correct?");
                            commandSender.sendMessage(ChatColor.GRAY+"Command usage: /tokens give <player name> <tokens amount>");
                        }
                    }else{
                        commandSender.sendMessage(ChatColor.GRAY+"You don't have "+ChatColor.GOLD+""+num+""+ChatColor.GRAY+" tokens.");
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED+"Invalid command use! Your arguments were "+ Arrays.toString(args));
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /tokens give <player name> <tokens amount>");
                }
                return true;
            }else{
                return false;
            }
        }else if(args[0].equalsIgnoreCase("reload")){
            if(!(commandSender instanceof Player) || commandSender.hasPermission("tokens.reload")) {
                if (args.length == 1) {
                    plugin.reloadConfig();
                    commandSender.sendMessage(ChatColor.GRAY + "Reloaded all the config files");
                } else if (args[1].equalsIgnoreCase("keys")) {
                    plugin.reloadKeys();
                    commandSender.sendMessage(ChatColor.GRAY+"Reloaded the key config");
                } else if (args[1].equalsIgnoreCase("lang")) {
                    plugin.reloadMessages();
                    commandSender.sendMessage(ChatColor.GRAY+"Reloaded the messages config");
                }
                return true;
            }
            return false;
        } else if (args[0].equalsIgnoreCase("buy") && plugin.vaultBuy) {
            if(commandSender instanceof Player){
                if(args.length==2){
                    vault.purchaseVault((Player) commandSender, Integer.parseInt(args[1]), plugin);
                }else{
                    commandSender.sendMessage(ChatColor.RED+"Invalid command use! Your arguments were "+ Arrays.toString(args));
                    commandSender.sendMessage(ChatColor.GRAY+"Command usage: /tokens buy <tokens amount>");
                }
            }else return false;
            return true;
        } else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
            commandSender.sendMessage(ChatColor.GREEN + "===============[ " + ChatColor.GOLD + "Tokens Help" + ChatColor.GREEN + " ]===============");
            commandSender.sendMessage("/tokens help " + ChatColor.GRAY + " Displays this helpful text");
            commandSender.sendMessage("/tokens" + ChatColor.GRAY + " Displays your number of tokens");
            commandSender.sendMessage("/tokens give <player name> <tokens amount>" + ChatColor.GRAY + " Gives tokens to another player");
            commandSender.sendMessage("/redeem" + ChatColor.GRAY + " Displays help using the redeem command");
            return true;
        }else if(args.length==1) {
            Player target = Bukkit.getPlayer(args[0]);
            if(commandSender instanceof Player) {
                if (commandSender.hasPermission("tokens.others")) {
                    if (target != null) {
                        commandSender.sendMessage(args[0] + ChatColor.GRAY + " has " + ChatColor.GOLD + plugin.handler.getTokens(target) + ChatColor.GRAY + " tokens.");
                    } else {
                        commandSender.sendMessage(ChatColor.RED+"Couldn't find player "+ ChatColor.GRAY + args[0] + ChatColor.RED +". did you spell their username correct?");
                    }
                    return true;
                } else {
                    return false;// Player without tokens.others perms ran the command
                }
            }else{
                if (target!=null){
                    commandSender.sendMessage(args[0] + " has " + plugin.handler.getTokens(target) + " tokens.");
                }else{
                    commandSender.sendMessage("Couldn't find a player with the name " + args[0]);
                }
                return true;
            }
        }else{
           return false;// Server used /tokens command
        }
    }
}
