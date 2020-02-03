package net.thirdshift.tokens.commands;

import net.thirdshift.tokens.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandTokens implements CommandExecutor {

    private Tokens plugin;

    public CommandTokens(Tokens instance){this.plugin=instance;}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(args[0].equalsIgnoreCase("add")) {
            if (commandSender instanceof Player) {
                int num = Integer.parseInt(args[1]);
                plugin.handler.addTokens((Player) commandSender, num);
            }else{
                Player target = Bukkit.getPlayer(args[1]);
                int num = Integer.parseInt(args[2]);
                plugin.handler.addTokens(target, num);
            }
            return true;
        }else if(args[0].equalsIgnoreCase("give")) {
            if(commandSender instanceof Player){
                if(args.length==3){
                    int num = Integer.parseInt(args[2]);
                    if(plugin.handler.getTokens((Player) commandSender) >= num) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            plugin.handler.removeTokens((Player) commandSender, num);
                            plugin.handler.addTokens(target, num);
                        } else {
                            commandSender.sendMessage("Couldn't find player " + args[1] + ". did you spell their username correct?");
                            commandSender.sendMessage("Try /tokens give <UserName> <Amount>");
                        }
                    }else{
                        commandSender.sendMessage("You don't have "+num+" tokens.");
                    }
                }else{
                    commandSender.sendMessage("Invalid command use! Your arguments were "+ Arrays.toString(args));
                    commandSender.sendMessage("Try /tokens give <UserName> <Amount>");
                }
                return true;
            }else{
                return false;
            }
        }else if(args[0].equalsIgnoreCase("reload")){
            if( !(commandSender instanceof Player)){
                plugin.reloadConfig();// Console ran the command
                return true;
            }else{
                if(commandSender.hasPermission("tokens.reload")){
                    plugin.reloadConfig();// Player with permission ran the command
                    return true;
                }else{
                    return false;// Player without permission ran the command
                }
            }
        }else{
            return false;
        }
    }
}
