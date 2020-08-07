package net.thirdshift.tokens.commands.tokens;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.util.PlayerListUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabTokens implements TabCompleter {

    Tokens plugin;

    public TabTokens(Tokens instance){
        this.plugin=instance;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        List<String> completions = new ArrayList<>();
        List<String> ret = new ArrayList<>();
        if(args.length==1){
            completions.add("give");
            completions.add("help");

            if(plugin.hasVault&&plugin.vaultEnabled&&plugin.vaultBuy)
                completions.add("buy");

            if(sender.hasPermission("tokens.add"))
                completions.add("add");
            if(sender.hasPermission("tokens.set"))
                completions.add("set");
            if(sender.hasPermission("tokens.remove"))
                completions.add("remove");
            if(sender.hasPermission("tokens.reload"))
                completions.add("reload");

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