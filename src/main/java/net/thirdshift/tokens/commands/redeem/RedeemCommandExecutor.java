package net.thirdshift.tokens.commands.redeem;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.messages.messageComponents.CombatMessageComponent;
import net.thirdshift.tokens.messages.messageComponents.MessageComponent;
import net.thirdshift.tokens.messages.messageComponents.SenderMessageComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RedeemCommandExecutor extends TokensCustomCommandExecutor {

    public RedeemCommandExecutor(final Tokens plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if(commandSender instanceof Player && commandSender.hasPermission("tokens.redeem")){
            if( plugin.getTokensCombatManager() != null && plugin.getTokensCombatManager().isInCombat( (Player) commandSender) ){
                if(!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
                    ArrayList<MessageComponent> components = new ArrayList<>();
                    components.add(new SenderMessageComponent(commandSender));
                    components.add(new CombatMessageComponent(plugin.getTokensCombatManager().secondsLeft((Player) commandSender)));
                    commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", components));
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

                try {
                    commandModules.get(commandName).onCommand(commandSender, actualArgs);
                    return true;
                } catch (NullPointerException ignored){
                }

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
