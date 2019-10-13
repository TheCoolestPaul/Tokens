package net.thirdshift.commands;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import net.thirdshift.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDTokens implements CommandExecutor {
	private Tokens plugin;
	public CMDTokens(Tokens instance){
		 plugin = instance;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (sender instanceof Player) {
    		if(args.length==0) {
    			if(plugin.combatLogXBlockTokens && plugin.combatLogXEnabled) {
    				if(CombatUtil.isInCombat((Player) sender)) {
	    				sender.sendMessage(ChatColor.RED+"You can't use tokens while in combat!");
	    				return true;
    				}
    			}
				int senderTokens = plugin.getTokens(((Player) sender));
	            sender.sendMessage("You have "+ChatColor.GOLD+""+senderTokens+""+ChatColor.WHITE+" tokens" );
	            return true;
    		}else if(args[0].equalsIgnoreCase("set") && sender.hasPermission("tokens.set")){
    			if(args[1]!=null && args[2]!=null) {
					Player target = Bukkit.getPlayer(args[1]);
					if(target!=null) {
						plugin.setTokens(target, Integer.parseInt(args[2]));
						sender.sendMessage("Set "+ChatColor.GRAY+""+target.getName()+"'s "+ChatColor.WHITE+"tokens to "+ChatColor.GOLD+""+args[2]);
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
    		}else if(args[0].equalsIgnoreCase("give")) {
    			if(plugin.combatLogXBlockTokens && plugin.combatLogXEnabled) {
    				if(CombatUtil.isInCombat((Player) sender)) {
	    				sender.sendMessage(ChatColor.RED+"You can't use tokens while in combat!");
	    				return true;
    				}
    			}
    			if(args.length == 3) {
					int senderTokens = plugin.getTokens(((Player) sender));
    				if(senderTokens >= Integer.parseInt(args[2])) {
    					Player target = Bukkit.getPlayer(args[1]);
	    				if(target!=null) {
	    					plugin.setTokens(target, (plugin.getTokens(target)+Integer.parseInt(args[2])));
	    					plugin.setTokens((Player) sender, (senderTokens-Integer.parseInt(args[2])));
	    					sender.sendMessage("You sent "+ChatColor.GOLD+""+args[2]+""+ChatColor.WHITE+" token(s) to "+ChatColor.GREEN+""+target.getName());
	    					target.sendMessage("You received "+ChatColor.GOLD+""+args[2]+""+ChatColor.WHITE+" token(s) from "+ChatColor.GREEN+""+sender.getName());
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
    		}else if(args[0].equalsIgnoreCase("add") && sender.hasPermission("tokens.add")) {
    			if(args.length == 3) {
    				Player target = Bukkit.getPlayer(args[1]);
    				if(target!=null) {
    					plugin.setTokens(target, (Integer.parseInt(args[2])+plugin.getTokens(target)));
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
    		}else if(args[0].equalsIgnoreCase("remove") && sender.hasPermission("tokens.remove")) {
    			if(args.length == 3) {
    				Player target = Bukkit.getPlayer(args[1]);
    				if(target!=null) {
    					int targetTokens = plugin.getTokens(target);
    					int toRemove = Integer.parseInt(args[2]);
    					if (targetTokens >= toRemove) {
    						plugin.setTokens(target, (targetTokens-toRemove));
    					}else {
    						plugin.setTokens(target, 0);
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
    		}else if(args[0].equalsIgnoreCase("help")){
				sender.sendMessage(ChatColor.GREEN+ "===============[ " +ChatColor.GOLD+ "Tokens Help"+ ChatColor.GREEN +" ]===============");
				sender.sendMessage("/tokens help "+ChatColor.GRAY+" Displays this helpful text");
				sender.sendMessage("/tokens"+ChatColor.GRAY+" Displays your number of tokens");
				sender.sendMessage("/redeem"+ChatColor.GRAY+" Displays help using the redeem command");
				return true;
			}
    		sender.sendMessage(ChatColor.RED+"Invalid command use");
    		sender.sendMessage(ChatColor.GRAY+"Use "+ChatColor.RED+"/tokens help"+ChatColor.GRAY+" for command usage");
    		return true;
        }
    	System.out.println("Console used the /tokens command!");
        return true;
    }
    
}
