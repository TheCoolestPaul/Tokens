package net.thirdshift.tokens.commands;

import net.thirdshift.tokens.Tokens;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTokensReload implements CommandExecutor {

    private Tokens plugin;

    public CommandTokensReload(Tokens instance){this.plugin=instance;}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if( !(commandSender instanceof Player)){
            plugin.reloadConfig();
            return true;
        }else{
            return false;
        }
    }
}
