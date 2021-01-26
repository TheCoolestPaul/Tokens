package net.thirdshift.tokens.commands.redeem;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RedeemCommandExecutor extends TokensCustomCommandExecutor {

    public RedeemCommandExecutor(Tokens plugin) {
        super(plugin);
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
                for(CommandModule redeemCommandModule : commandModules.values()){
                    addons.append(redeemCommandModule.getCommand());
                    addons.append(" ");
                }
                commandSender.sendMessage(ChatColor.GRAY+"Command usage: /redeem < " + addons.toString() + ">");
                return true;
            } else {
                String commandName = args[0];
                String[] actualArgs = new String[args.length-1];
                System.arraycopy(args, 1, actualArgs, 0, args.length - 1);

                for(CommandModule redeemCommandModule : commandModules.values()){
                    if (commandName.equalsIgnoreCase(redeemCommandModule.getCommand())){
                        redeemCommandModule.onCommand( commandSender, actualArgs);
                        return true;
                    }
                    for(String alias : redeemCommandModule.getCommandAliases()){
                        if(commandName.equalsIgnoreCase(alias)){
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
