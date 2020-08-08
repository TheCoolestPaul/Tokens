package net.thirdshift.tokens.commands.tokens;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import net.thirdshift.tokens.commands.redeem.redeemcommands.VaultRedeemModule;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import net.thirdshift.tokens.messages.messageData.PlayerTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandTokens implements CommandExecutor {

    private final Tokens plugin;
    private final TokensHandler tokensHandler;

    public CommandTokens(Tokens instance){
        this.plugin=instance;
        tokensHandler=instance.getHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length==0){
            if(commandSender instanceof Player){
                if(plugin.messageHandler.getMessage("tokens.main").isEmpty()){
                    return false;
                }
                List<Object> objects = new ArrayList<>();
                objects.add(new PlayerSender((Player) commandSender));
                objects.add(plugin.getHandler().getTokens((Player) commandSender));
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
                            tokensHandler.addTokens(target, num);
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
                    tokensHandler.addTokens(target, num);
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
                                objects.add(new PlayerSender(commandSender));
                                commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", objects));
                            }
                            return true;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target!=null){
                            int num = Integer.parseInt(args[2]);
                            tokensHandler.setTokens(target, num);
                            List<Object> objects = new ArrayList<>();
                            objects.add(num);
                            objects.add(new PlayerSender(commandSender));
                            objects.add(new PlayerTarget(target));
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.set.sender", objects));
                            target.sendMessage(plugin.messageHandler.useMessage("tokens.set.receiver", objects));
                        }else{
                            if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                                List<Object> objects = new ArrayList<>();
                                objects.add(new PlayerSender(commandSender));
                                objects.add(new PlayerTarget(args[1]));
                                commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                            }
                        }
                    }
                }else{
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target!=null){
                        int num = Integer.parseInt(args[2]);
                        tokensHandler.setTokens(target, num);
                        List<Object> objects = new ArrayList<>();
                        objects.add(num);
                        objects.add(new PlayerSender(commandSender));
                        objects.add(new PlayerTarget(target));
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.set.sender", objects));
                        target.sendMessage(plugin.messageHandler.useMessage("tokens.receiver", objects));
                    }else{
                        if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender(commandSender.getName()));
                            objects.add(new PlayerTarget(args[1]));
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                        }
                    }
                }
            }else{
                if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.message").isEmpty()){
                    List<Object> objects = new ArrayList<>();
                    objects.add(new PlayerSender((Player)commandSender));
                    objects.add("/tokens set <player name> <tokens amount>");
                    commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.message", objects));
                }
                if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command-correction").isEmpty()){
                    List<Object> objects = new ArrayList<>();
                    objects.add(new PlayerSender((Player)commandSender));
                    objects.add("/tokens set <player name> <tokens amount>");
                    commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
                }
            }
            return true;
        }else if (args[0].equalsIgnoreCase("remove")){
            if(commandSender instanceof Player){
                if(commandSender.hasPermission("tokens.remove")){
                    if(args.length==3){
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target!=null){
                            int num = Integer.parseInt(args[2]);
                            plugin.getHandler().removeTokens(target, num);
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender(commandSender));
                            objects.add(new PlayerTarget(target));
                            objects.add(num);
                            if(!plugin.messageHandler.getMessage("tokens.remove.sender").isEmpty()){
                                commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.remove.sender", objects));
                            }
                            if(!plugin.messageHandler.getMessage("tokens.remove.receiver").isEmpty()){
                                target.sendMessage(plugin.messageHandler.useMessage("tokens.remove.receiver", objects));
                            }
                        }else{
                            if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                                List<Object> objects = new ArrayList<>();
                                objects.add(new PlayerSender(commandSender));
                                objects.add(new PlayerTarget(args[1]));
                                commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                            }
                        }
                    }else{
                        if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.message").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender(commandSender));
                            objects.add("/tokens remove <player name> <tokens amount>");
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.message", objects));
                        }
                        if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command-correction").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender(commandSender));
                            objects.add("/tokens remove <player name> <tokens amount>");
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
                        }
                    }
                }else return false;
            }else{
                if(args.length==3){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target!=null){
                        int num = Integer.parseInt(args[2]);
                        plugin.getHandler().removeTokens(target, num);
                        List<Object> objects = new ArrayList<>();
                        objects.add(num);
                        objects.add(new PlayerSender(commandSender));
                        objects.add(new PlayerTarget(target));
                        if(!plugin.messageHandler.getMessage("tokens.set.sender").isEmpty()) {
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.set.sender", objects));
                        }
                        if(plugin.messageHandler.getMessage("tokens.set.receiver").isEmpty()){
                            target.sendMessage(plugin.messageHandler.useMessage("tokens.set.receiver", objects));
                        }
                    }else{
                        if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender(commandSender.getName()));
                            objects.add(new PlayerTarget(args[1]));
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                        }
                    }
                }else{
                    if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command.message").isEmpty()){
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerSender(commandSender));
                        objects.add("/tokens remove <player name> <tokens amount>");
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.message", objects));
                    }
                    if(!plugin.messageHandler.getMessage("tokens.errors.invalid-command-correction").isEmpty()){
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerSender(commandSender));
                        objects.add("/tokens remove <player name> <tokens amount>");
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command-correction", objects));
                    }
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
                    if(tokensHandler.getTokens((Player) commandSender) >= num) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            if(target.equals(commandSender)){
                                commandSender.sendMessage(ChatColor.RED+"You can't give tokens to yourself.");
                                return true;
                            }
                            if(num <= 0 ){
                                commandSender.sendMessage(ChatColor.RED+"You can't do that.");
                                return true;
                            }
                            plugin.getHandler().removeTokens((Player) commandSender, num);
                            plugin.getHandler().addTokens(target, num);
                            PlayerSender sender = new PlayerSender((Player) commandSender);
                            PlayerTarget playerTarget = new PlayerTarget(target);
                            List<Object> stuff = new ArrayList<>();
                            stuff.add(sender);
                            stuff.add(playerTarget);
                            stuff.add(num);
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.give.sender", stuff));
                            target.sendMessage(plugin.messageHandler.useMessage("tokens.give.receiver", stuff));
                        } else {
                            if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                                List<Object> objects = new ArrayList<>();
                                objects.add(new PlayerSender(commandSender.getName()));
                                objects.add(new PlayerTarget(args[1]));
                                commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                            }
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
                    VaultRedeemModule.purchaseVault((Player) commandSender, Integer.parseInt(args[1]), plugin);
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
            Player target = Bukkit.getPlayerExact(args[0]);
            if(commandSender instanceof Player) {
                if (commandSender.hasPermission("tokens.others")) {
                    if (target != null) {
                        if(!plugin.messageHandler.getMessage("tokens.others").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerTarget(target));
                            objects.add(new PlayerSender(commandSender));
                            objects.add(tokensHandler.getTokens(target));
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.others", objects));
                        }
                    } else {
                        if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender(commandSender.getName()));
                            objects.add(new PlayerTarget(args[0]));
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                        }
                    }
                    return true;
                } else {
                    return false;// Player without tokens.others perms ran the command
                }
            }else{
                if (target!=null){
                    if(!plugin.messageHandler.getMessage("tokens.others").isEmpty()){
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerTarget(target));
                        objects.add(new PlayerSender(commandSender));
                        objects.add(tokensHandler.getTokens(target));
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.others", objects));
                    }
                }else{
                    if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerSender(commandSender.getName()));
                        objects.add(new PlayerTarget(args[0]));
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                    }
                }
                return true;
            }
        }else{
           return false;
        }
    }
}
