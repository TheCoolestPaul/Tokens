package net.thirdshift.tokens.commands.tokens;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.TokensHandler;
import net.thirdshift.tokens.cache.TokenCache;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import net.thirdshift.tokens.commands.tokens.tokenscommands.*;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import net.thirdshift.tokens.messages.messageData.PlayerTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TokensCommandExecutor extends TokensCustomCommandExecutor {

    private final TokensHandler tokensHandler;

    // Temporary variables for TESTING PURPOSES!
    private final AddTokensCommandModule addTokensCommandModule;
    private final ReloadTokensCommandModule reloadTokensCommandModule;
    private final GiveTokensCommandModule giveTokensCommandModule;
    private final BuyTokensCommandModule buyTokensCommandModule;
    private final SetTokensCommandModule setTokensCommandModule;
    private final RemoveTokensCommandModule removeTokensCommandModule;

    public TokensCommandExecutor(Tokens instance){
        super(instance);
        tokensHandler=instance.getHandler();
        addTokensCommandModule = new AddTokensCommandModule();
        reloadTokensCommandModule = new ReloadTokensCommandModule();
        giveTokensCommandModule = new GiveTokensCommandModule();
        buyTokensCommandModule = new BuyTokensCommandModule();
        setTokensCommandModule = new SetTokensCommandModule();
        removeTokensCommandModule = new RemoveTokensCommandModule();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (commandSender instanceof Player){
            if( plugin.getTokensConfigHandler().isRunningCombatLogX() && plugin.getTokensConfigHandler().getTokensCombatManager().isInCombat((Player) commandSender) ){
                if(!plugin.messageHandler.getMessage("combatlogx.deny").isEmpty()) {
                    List<Object> objects = new ArrayList<>();
                    objects.add(new PlayerSender((Player) commandSender));
                    commandSender.sendMessage(plugin.messageHandler.useMessage("combatlogx.deny", objects));
                }
            }
        }
        if(args.length==0){
            if(commandSender instanceof Player){
                if(plugin.messageHandler.getMessage("tokens.main").isEmpty()){
                    return false;
                }
                List<Object> objects = new ArrayList<>();
                objects.add(new PlayerSender((Player) commandSender));
                objects.add(plugin.getHandler().getTokens((Player) commandSender));
                commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.main", objects));
			}
            else {
            	commandSender.sendMessage(plugin.messageHandler.formatMessage("tokens.console"));
            	displayTokenHelp( commandSender );
			}
			return true;
		}
        if(args[0].equalsIgnoreCase("add") ) {
			String[] actualArgs = new String[args.length-1];
			System.arraycopy(args, 1, actualArgs, 0, args.length - 1);
			addTokensCommandModule.onCommand(commandSender, actualArgs);
            return true;
        } else if (args[0].equalsIgnoreCase("reload")){
        	String[] actualArgs = new String[args.length-1];
			System.arraycopy(args, 1, actualArgs, 0, args.length - 1);
            reloadTokensCommandModule.onCommand(commandSender, actualArgs);
            return true;
        } else if(args[0].equalsIgnoreCase("set")){
			String[] actualArgs = new String[args.length-1];
			System.arraycopy(args, 1, actualArgs, 0, args.length - 1);
			setTokensCommandModule.onCommand(commandSender, actualArgs);
            return true;
        }else if (args[0].equalsIgnoreCase("remove")){
			String[] actualArgs = new String[args.length-1];
			System.arraycopy(args, 1, actualArgs, 0, args.length - 1);
            removeTokensCommandModule.onCommand(commandSender, actualArgs);
            return true;
        }else if(args[0].equalsIgnoreCase("give")) {
			String[] actualArgs = new String[args.length-1];
			System.arraycopy(args, 1, actualArgs, 0, args.length - 1);
            giveTokensCommandModule.onCommand(commandSender, actualArgs);
            return true;
        } else if (args[0].equalsIgnoreCase("buy") && plugin.getTokensConfigHandler().isVaultBuy()) {
			String[] actualArgs = new String[args.length-1];
			System.arraycopy(args, 1, actualArgs, 0, args.length - 1);
			buyTokensCommandModule.onCommand(commandSender, actualArgs);
            return true;
        } 
        else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
        	displayTokenHelp( commandSender );
        	
            return true;
        }
        
        else if ( args[0].equalsIgnoreCase("cache") && 
        		(commandSender.hasPermission("tokens.cache") || commandSender.isOp()) ) {
        	
        	return commandsTokenCache( commandSender, args );
        }
        
        else if(args.length==1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if(commandSender instanceof Player) {
                if (commandSender.hasPermission("tokens.others")) {
                    if (target != null) {
                        if(!plugin.messageHandler.getMessage("tokens.others").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerTarget(target));
                            objects.add(new PlayerSender(commandSender));
                            objects.add(tokensHandler.getTokens(target));
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.others", objects));
                        }
                    } else {
                        if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                            List<Object> objects = new ArrayList<>();
                            objects.add(new PlayerSender(commandSender.getName()));
                            objects.add(new PlayerTarget(args[0]));
                            commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }else{
                if (target!=null){
                    if(!plugin.messageHandler.getMessage("tokens.others").isEmpty()){
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerTarget(target));
                        objects.add(new PlayerSender(commandSender));
                        objects.add(tokensHandler.getTokens(target));
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.others", objects));
                    }
                }else{
                    if(!plugin.messageHandler.getMessage("tokens.errors.no-player").isEmpty()){
                        List<Object> objects = new ArrayList<>();
                        objects.add(new PlayerSender(commandSender.getName()));
                        objects.add(new PlayerTarget(args[0]));
                        commandSender.sendMessage(plugin.messageHandler.useMessage("tokens.errors.no-player", objects));
                    }
                }
                return true;
            }
        } 

        else{
        	displayTokenHelp( commandSender );
           return false;
        }
    }

	private boolean commandsTokenCache( CommandSender commandSender, String[] args )
	{
		if ( args.length > 1 && args[1].equalsIgnoreCase("sync") ) {
		    commandSender.sendMessage(plugin.messageHandler.formatMessage("tokens.cache.sync.start.message"));
		    TokenCache.getInstance().submitAsyncSynchronizePlayers( commandSender );
		}
		else if ( args.length > 2 && args[1].equalsIgnoreCase("stats") && args[2].equalsIgnoreCase("toggle") ) {
			TokenCache.getInstance().getStats().toggleEnabled();
			commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.toggled",
					TokenCache.getInstance().getStats().isEnabled()) );
		}
		else if ( args.length > 2 && args[1].equalsIgnoreCase("stats") && args[2].equalsIgnoreCase("clear") ) {
			TokenCache.getInstance().getStats().clear();
			commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.cleared") );
		}
		else if ( args.length > 2 && args[1].equalsIgnoreCase("stats") && args[2].equalsIgnoreCase("dump") ) {
			String playerCacheDump = TokenCache.getInstance().getPlayerDumpStats();
			commandSender.sendMessage( playerCacheDump );
		}
		else if ( args.length > 2 && args[1].equalsIgnoreCase("stats") && args[2].equalsIgnoreCase("journaling") ) {
			TokenCache.getInstance().toggleJournal();
			if ( TokenCache.getInstance().isJournal() ) {
				commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.journaling-enabled",
						TokenCache.getInstance().isJournal()) );
				if ( args.length > 3 ) {
					TokenCache.getInstance().setJournalPlayer( args[3] );
					
					commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.journaling-player",
							TokenCache.getInstance().getJournalPlayer()) );
				}
			}
			else {
				commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.journaling-disabled",
						TokenCache.getInstance().getStats().isEnabled()) );
				TokenCache.getInstance().setJournalPlayer( null );
			}
			
		}
		else if ( args.length > 1 && args[1].equalsIgnoreCase("stats") ) {
			commandSender.sendMessage( TokenCache.getInstance().getStats().displayStats() );
		}
		else {
		    commandSender.sendMessage(ChatColor.GREEN + "==========[ " + ChatColor.GOLD + "Tokens Cache Help " +
		    			ChatColor.BLUE + plugin.getDescription().getVersion() + ChatColor.GREEN + " ]==========");
		    commandSender.sendMessage(ChatColor.AQUA + "/tokens cache help " + plugin.messageHandler.formatMessage("tokens.cache.menu.help-help"));
		    commandSender.sendMessage(ChatColor.AQUA + "/tokens cache sync " + plugin.messageHandler.formatMessage("tokens.cache.menu.help-sync"));
		    commandSender.sendMessage(ChatColor.AQUA + "/tokens cache stats " + plugin.messageHandler.formatMessage("tokens.cache.menu.help-stats"));
		    commandSender.sendMessage(ChatColor.AQUA + "/tokens cache stats toggle " + plugin.messageHandler.formatMessage("tokens.cache.menu.help-stats-toggle"));
		    commandSender.sendMessage(ChatColor.AQUA + "/tokens cache stats clear " + plugin.messageHandler.formatMessage("tokens.cache.menu.help-stats-clear"));
		    commandSender.sendMessage(ChatColor.AQUA + "/tokens cache stats dump " + plugin.messageHandler.formatMessage("tokens.cache.menu.help-stats-dump"));
		    commandSender.sendMessage(ChatColor.AQUA + "/tokens cache stats journaling [playerName] " + plugin.messageHandler.formatMessage("tokens.cache.menu.help-stats-journaling"));

		}
		return true;
	}
    
    private void displayTokenHelp( CommandSender commandSender ) {
    	boolean isConsole = !(commandSender instanceof Player);
    	
        commandSender.sendMessage(ChatColor.GREEN + "===============[ " + ChatColor.GOLD + "Tokens Help " +
    								ChatColor.BLUE + plugin.getDescription().getVersion() + ChatColor.GREEN + " ]===============");
        commandSender.sendMessage(ChatColor.AQUA + "/tokens help " + ChatColor.GRAY + " Displays this helpful text");
        commandSender.sendMessage(ChatColor.AQUA + "/tokens" + ChatColor.GRAY + " Displays your number of tokens");
        if (commandSender.hasPermission("tokens.add") || isConsole) {
        	commandSender.sendMessage(ChatColor.AQUA + "/tokens add <player name> <tokens amount>" + ChatColor.GRAY + " Adds tokens to a player");
        }
        
        commandSender.sendMessage(ChatColor.AQUA + "/tokens give <player name> <tokens amount>" + ChatColor.GRAY + " Gives your tokens to another player");
        if(commandSender.hasPermission("tokens.remove") || isConsole) {
        	commandSender.sendMessage(ChatColor.AQUA + "/tokens remove <player name> <tokens amount>" + ChatColor.GRAY + " Remove tokens from a player");
        }
        
        commandSender.sendMessage(ChatColor.AQUA + "/redeem" + ChatColor.GRAY + " Displays help using the redeem command");
        if(commandSender.hasPermission("tokens.cache") || isConsole) {
        	commandSender.sendMessage(ChatColor.AQUA + "/tokens cache " + ChatColor.GRAY + " Displays the token cache sub-commands");
        }
    }
}
