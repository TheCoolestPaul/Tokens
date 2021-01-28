package net.thirdshift.tokens.commands.tokens;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.util.PlayerListUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabTokens implements TabCompleter {

    private final TokensCommandExecutor commandExecutor;

    public TabTokens(final Tokens plugin){
        commandExecutor= plugin.getTokensCommandExecutor();
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args){
        List<String> completions = new ArrayList<>(); // All possible completions
        List<String> ret = new ArrayList<>();// Ret the closest of all of them

        if(args.length==1){

            for(CommandModule redeemCommandModule :  commandExecutor.getCommandModules().values()){
                if (redeemCommandModule.getPermission() == null || sender.hasPermission(redeemCommandModule.getPermission()))
                    completions.add(redeemCommandModule.getCommand());
            }
            StringUtil.copyPartialMatches(args[0], completions, ret);

            if(sender.hasPermission("tokens.others")){
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                List<String> playerNames = new ArrayList<>();
                players.forEach((ply)-> playerNames.add(ply.getName()));
                completions.addAll(playerNames);
            }
            StringUtil.copyPartialMatches(args[0], completions, ret);
        }else if(args.length==2){
            if(args[0].equalsIgnoreCase("give"))
                return StringUtil.copyPartialMatches(args[1], PlayerListUtil.playerListUtil((Player) sender, false), ret);
            if(args[0].equalsIgnoreCase("add") && sender.hasPermission("tokens.add"))
                return StringUtil.copyPartialMatches(args[1], PlayerListUtil.playerListUtil((Player) sender, true), ret);
            if(args[0].equalsIgnoreCase("set") && sender.hasPermission("tokens.set"))
                return StringUtil.copyPartialMatches(args[1], PlayerListUtil.playerListUtil((Player) sender, true), ret);
            if(args[0].equalsIgnoreCase("remove") && sender.hasPermission("tokens.remove"))
                return StringUtil.copyPartialMatches(args[1], PlayerListUtil.playerListUtil((Player) sender, true), ret);
        }
        return ret;
    }
}
