package net.thirdshift.tokens.commands;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.redeem.mcmmo;
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
            if(args.length==0){
                commandSender.sendMessage("Command usage: /redeem <"+plugin.getAddons()+">");
            }else if(args.length==1){
                if(args[0].equalsIgnoreCase("mcmmo")){
                    commandSender.sendMessage("Command usage: /redeem mcmmo <skill name> <tokens to spend>");
                }else if(args[0].equalsIgnoreCase("factions")){
                    commandSender.sendMessage("Command usage: /redeem faction <tokens to spend>");
                }else if(args[0].equalsIgnoreCase("money")){
                    commandSender.sendMessage("Command usage: /redeem money <tokens to spend>");
                }else{
                    commandSender.sendMessage("Invalid redeemable: "+ Arrays.toString(args));
                }
            }else if(args[0].equalsIgnoreCase("mcmmo")){
                if(args.length==3){
                    mcmmo.redeemMCMMO((Player)commandSender, args[1], Integer.parseInt(args[2]), plugin);
                }else{
                    commandSender.sendMessage("Invalid command use.");
                    commandSender.sendMessage("Command usage: /redeem mcmmo <skill name> <tokens to spend>");
                }
            }
         return true;
        }else {
            return false;
        }
    }
}
