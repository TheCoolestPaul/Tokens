package net.thirdshift.tokens.commands.redeem;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.redeem.redeemcommands.RedeemModule;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
            plugin.getLogger().info("A module already exists with command " + redeemModule.getCommand());
        } else {
            commandRedeemMap.put(redeemModule.getCommand(), redeemModule);
            plugin.getLogger().info("Added module "+ redeemModule.getCommand());
        }
    }

    public HashMap<String, RedeemModule> getRedeemModules() {
        return commandRedeemMap;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player && commandSender.hasPermission("tokens.redeem")){
            if( plugin.getTokensConfigHandler().isRunningCombatLogX() && plugin.getTokensConfigHandler().getTokensCombatManager().isInCombat((Player) commandSender) ){
                if(!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
                    List<Object> objects = new ArrayList<>();
                    objects.add(new PlayerSender((Player) commandSender));
                    commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", objects));
                }
                return true;
            }
            if (args.length == 0) {
                StringBuilder addons = new StringBuilder();
                for(RedeemModule redeemModule : commandRedeemMap.values()){
                    addons.append(redeemModule.getCommand());
                    addons.append(" ");
                }
                commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem < " + addons.toString() + ">");
                return true;
            } else {
                for(RedeemModule redeemModule : commandRedeemMap.values()){
                    String com = args[0];
                    ArrayList<String> actualArgs = new ArrayList<>();
                    for (String arg : args){
                        if(!arg.equalsIgnoreCase(com))
                            actualArgs.add(arg);
                    }
                    if (com.equalsIgnoreCase(redeemModule.getCommand())){
                        redeemModule.redeem((Player) commandSender, actualArgs);
                        return true;
                    }
                    for(String alias : redeemModule.getCommandAliases()){
                        if(com.equalsIgnoreCase(alias)){
                            redeemModule.redeem((Player) commandSender, actualArgs);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
