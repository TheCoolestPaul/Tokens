package main.java.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.EventUtils;
import main.java.Tokens;

public class CMDTokens implements CommandExecutor {
	Tokens plugin = null;
	 public CMDTokens(Tokens instance){
		 plugin = instance;
    }
	// This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (sender instanceof Player) {
    		if(args.length==0) {
	            sender.sendMessage("You have "+ChatColor.GOLD+""+plugin.getRDatabase().getTokens(((Player) sender).getUniqueId())+""+ChatColor.WHITE+" tokens" );
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
    		}else if(args[0].equalsIgnoreCase("redeem")) {
    			if(args[1]!=null && args[2]!=null) {
	    			PrimarySkillType skill;
	    			if( (PrimarySkillType.getSkill(args[1])) != null){
	    				if(plugin.getRDatabase().getTokens(((Player) sender).getUniqueId())>=Integer.parseInt(args[2])) {
	    					skill = PrimarySkillType.getSkill(args[1]);
	    					McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer((Entity) sender);
	    					senderMcMMO.addLevels(skill, Integer.parseInt(args[2]));
	    					return true;
	    				}else {
	    					sender.sendMessage(ChatColor.RED+"You don't have enough tokens for that");
	    					return true;
	    				}
	    			}else {
	    				List<String> skillList = PrimarySkillType.SKILL_NAMES;
	    				sender.sendMessage(ChatColor.RED+"Invalid skill McMMO skill");
	    				sender.sendMessage(ChatColor.RED+"Skills avaliable are "+ChatColor.GRAY+""+skillList);
	    				return true;
	    			}
    			}else {
    				sender.sendMessage(ChatColor.RED+"Invalid command use");
    				sender.sendMessage(ChatColor.RED+"/tokens redeem <skill name> <token amount>");
    				return true;
    			}
    		}else if(args[0].equalsIgnoreCase("give")) {
    			//TODO: give your tokens to another player
    			return true;
    		}else if(args[0].equalsIgnoreCase("add")) {
    			if(args[1]!=null) {
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
    		}
    		sender.sendMessage("Invalid command use");
    		return true;// catch all sort of thing.
        }
    	System.out.println("Console used the /tokens command!");
        return true;
    }
    
}
