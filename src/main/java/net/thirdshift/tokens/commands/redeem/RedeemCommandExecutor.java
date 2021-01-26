package net.thirdshift.tokens.commands.redeem;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.redeem.redeemcommands.RedeemCommandModule;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RedeemCommandExecutor implements CommandExecutor {
    private final Tokens plugin;
    private final HashMap<String, RedeemCommandModule> commandRedeemMap;

    public RedeemCommandExecutor(final Tokens plugin){
        this.plugin=plugin;
        commandRedeemMap = new HashMap<>();
    }

    public void registerRedeemModule(final RedeemCommandModule redeemCommandModule){
        if (commandRedeemMap.get(redeemCommandModule.getCommand())!=null){
            plugin.getLogger().info("A module already exists with command " + redeemCommandModule.getCommand());
        } else {
            commandRedeemMap.put(redeemCommandModule.getCommand(), redeemCommandModule);
            plugin.getLogger().info("Added module "+ redeemCommandModule.getCommand());
        }
    }

    public HashMap<String, RedeemCommandModule> getRedeemModules() {
        return commandRedeemMap;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
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
                for(RedeemCommandModule redeemCommandModule : commandRedeemMap.values()){
                    addons.append(redeemCommandModule.getCommand());
                    addons.append(" ");
                }
                commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem < " + addons.toString() + ">");
                return true;
            } else {
                for(RedeemCommandModule redeemCommandModule : commandRedeemMap.values()){
                    String com = args[0];
                    ArrayList<String> actualArgs = new ArrayList<>();
                    for (String arg : args){
                        if(!arg.equalsIgnoreCase(com))
                            actualArgs.add(arg);
                    }
                    if (com.equalsIgnoreCase(redeemCommandModule.getCommand())){
                        redeemCommandModule.onCommand( commandSender, actualArgs);
                        return true;
                    }
                    for(String alias : redeemCommandModule.getCommandAliases()){
                        if(com.equalsIgnoreCase(alias)){
                            redeemCommandModule.onCommand( commandSender, actualArgs);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
