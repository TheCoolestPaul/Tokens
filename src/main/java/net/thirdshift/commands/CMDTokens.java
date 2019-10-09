package net.thirdshift.commands;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import net.thirdshift.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CMDTokens implements CommandExecutor {
	Tokens plugin = null;
	 public CMDTokens(Tokens instance){
		 plugin = instance;
    }
	// This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (sender instanceof Player) {
    		int senderTokens = plugin.getRDatabase().getTokens(((Player) sender).getUniqueId());
    		if(args.length==0) {
    			if(plugin.combatLogXBlockTokens == true && plugin.combatLogXEnabled == true) {
    				if(CombatUtil.isInCombat((Player) sender)) {
	    				sender.sendMessage(ChatColor.RED+"You can't use tokens while in combat!");
	    				return true;
    				}
    			}
	            sender.sendMessage("You have "+ChatColor.GOLD+""+senderTokens+""+ChatColor.WHITE+" tokens" );
	            return true;
    		}else if(args[0].equalsIgnoreCase("set")){
    			if(args[1]!=null && args[2]!=null) {
	    			if(args[1]!=null) {
	    				Player target = Bukkit.getPlayer(args[1]);
	    				if(target!=null) {
	    					plugin.getRDatabase().setTokens(target.getUniqueId(), Integer.parseInt(args[2]));
	    					return true;
	    				}else {
	    					sender.sendMessage(ChatColor.RED+"Targeted player was invalid!");
	    					return true;
	    				}
	    			}else {
	    				sender.sendMessage(ChatColor.RED+"Invalid command use");
	    				sender.sendMessage(ChatColor.RED+"/tokens set <player name> <token amount>");
	    				return true;
	    			}
    			}else {
    				sender.sendMessage(ChatColor.RED+"Invalid command use");
    				sender.sendMessage(ChatColor.RED+"/tokens set <player name> <token amount>");
    				return true;
    			}
    		}else if(args[0].equalsIgnoreCase("give")) {
    			if(plugin.combatLogXBlockTokens == true && plugin.combatLogXEnabled == true) {
    				if(CombatUtil.isInCombat((Player) sender)) {
	    				sender.sendMessage(ChatColor.RED+"You can't use tokens while in combat!");
	    				return true;
    				}
    			}
    			if(args.length == 3) {
    				if(senderTokens >= Integer.parseInt(args[2])) {
    					Player target = Bukkit.getPlayer(args[1]);
	    				if(target!=null) {
	    					plugin.getRDatabase().setTokens(target.getUniqueId(), plugin.getRDatabase().getTokens(target.getUniqueId())+Integer.parseInt(args[2]));
	    					plugin.getRDatabase().setTokens(((Entity) sender).getUniqueId(), senderTokens-Integer.parseInt(args[2]));
	    					sender.sendMessage("You sent "+ChatColor.GOLD+""+args[2]+""+ChatColor.WHITE+" token(s) to "+ChatColor.GREEN+""+target.getName());
	    					target.sendMessage("You recieved "+ChatColor.GOLD+""+args[2]+""+ChatColor.WHITE+" token(s) from "+ChatColor.GREEN+""+sender.getName());
	    					return true;
	    				}else {
	    					sender.sendMessage(ChatColor.RED+"Targeted player was invalid!");
	    					return true;
	    				}
    				}else {
    					sender.sendMessage(ChatColor.RED+"You don't that many tokens to give!");
    					return true;
    				}
    			}else {
    				sender.sendMessage(ChatColor.RED+"Invalid command use");
    				sender.sendMessage(ChatColor.RED+"/tokens give <player name> <token amount>");
    				return true;
    			}
    		}else if(args[0].equalsIgnoreCase("add")) {
    			if(args.length == 3) {
    				Player target = Bukkit.getPlayer(args[1]);
    				if(target!=null) {
    					// Long but gets the job done ¯\_(ツ)_/¯
    					plugin.getRDatabase().setTokens(target.getUniqueId(), (Integer.parseInt(args[2])+plugin.getRDatabase().getTokens(target.getUniqueId())));
    					sender.sendMessage(ChatColor.GREEN+"Added "+ChatColor.GOLD+""+args[2]+""+ChatColor.WHITE+" to "+ChatColor.GRAY+""+target.getName()+""+ChatColor.WHITE+"'s tokens");
    					return true;
    				}else {
    					sender.sendMessage(ChatColor.RED+"Targeted player was invalid!");
    					return true;
    				}
    			}else {
    				sender.sendMessage(ChatColor.RED+"Invalid command use");
    				sender.sendMessage(ChatColor.RED+"/tokens add <player name> <token amount>");
    				return true;
    			}
    		}else if(args[0].equalsIgnoreCase("remove")) {
    			if(args.length == 3) {
    				Player target = Bukkit.getPlayer(args[1]);
    				if(target!=null) {
    					int targetTokens = plugin.getRDatabase().getTokens(target.getUniqueId());
    					int toRemove = Integer.parseInt(args[2]);
    					if (targetTokens >= toRemove) {
    						plugin.getRDatabase().setTokens(target.getUniqueId(), targetTokens-toRemove);
    					}else {
    						plugin.getRDatabase().setTokens(target.getUniqueId(), 0);
    					}
    					sender.sendMessage(ChatColor.RED+"Removed "+ChatColor.GOLD+""+toRemove+""+ChatColor.WHITE+" from "+ChatColor.GRAY+""+target.getName()+""+ChatColor.WHITE+"'s tokens");
    					return true;
    				}else {
    					sender.sendMessage(ChatColor.RED+"Targeted player was invalid!");
    					return true;
    				}
    			}else {
    				sender.sendMessage(ChatColor.RED+"Invalid command use");
    				sender.sendMessage(ChatColor.RED+"/tokens remove <player name> <token amount>");
    				return true;
    			}
    		}
    		sender.sendMessage("Invalid command use");
    		return true;// catch all sort of thing.
        }
    	System.out.println("Console used the /tokens command!");
        return true;
    }
    
}
