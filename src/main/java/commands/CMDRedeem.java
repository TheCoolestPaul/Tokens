package main.java.commands;

import java.util.List;

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

public class CMDRedeem implements CommandExecutor {
	Tokens plugin = null;
	 public CMDRedeem(Tokens instance){
		 plugin = instance;
    }
	// This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (sender instanceof Player) {
    		if(args.length != 0) {
				if(args[0]!= null && (args[0].equalsIgnoreCase("factions") || args[0].equalsIgnoreCase("faction")) ) {
					//do faction shit
					return true;
				}else if(args[0]!=null && args[0].equalsIgnoreCase("mcmmo")) {
					if(args.length == 3) {
						PrimarySkillType skill;
						int toRedeem = Integer.parseInt(args[2]);
						if(plugin.getRDatabase().getTokens(((Player) sender).getUniqueId()) >= toRedeem) {
							if( (PrimarySkillType.getSkill(args[1])) != null){
								if(plugin.getRDatabase().getTokens(((Player) sender).getUniqueId())>=Integer.parseInt(args[2])) {
									skill = PrimarySkillType.getSkill(args[1]);
									McMMOPlayer senderMcMMO = EventUtils.getMcMMOPlayer((Entity) sender);
									senderMcMMO.addLevels(skill, toRedeem);
									sender.sendMessage("You successfully redeemed "+ChatColor.GOLD+""+toRedeem+""+ChatColor.WHITE+" token(s) to the mcMMO skill "+ChatColor.GRAY+""+skill.getName());
									plugin.getRDatabase().setTokens(((Player) sender).getUniqueId(), plugin.getRDatabase().getTokens(((Player) sender).getUniqueId())-toRedeem);
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
							sender.sendMessage(ChatColor.RED+"You don't that many tokens to  redeem!");
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.RED+"Invalid command use");
						sender.sendMessage(ChatColor.RED+"/redeem mcmmo <skill name> <token amount>");
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.RED+"Invalid command use");
					sender.sendMessage(ChatColor.RED+"/redeem mcmmo <skill name> <token amount");
					return true;
				}
    		}else {
    			sender.sendMessage(ChatColor.RED+"Invalid command use");
				sender.sendMessage(ChatColor.RED+"/redeem "+whatHooks());
				return true;
    		}
        }
    	System.out.println("Console used the /redeem command!");
        return true;
    }
    
    private String whatHooks() {
    	String response = "";
    	if(plugin.hasFactions && plugin.hasMCMMO) {
    		response = "<mcmmo | factions>";
    	}else if(plugin.hasFactions && !plugin.hasMCMMO) {
    		response = "factions";
    	}else if(!plugin.hasFactions && plugin.hasMCMMO) {
    		response = "mcmmo";
    	}else {
    		//has none?
    		response="Contact server admin";
    	}
    	return response;
    }
    
}
