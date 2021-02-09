package net.thirdshift.tokens.commands.tokens.tokenscommands;

import net.thirdshift.tokens.cache.TokenCache;
import net.thirdshift.tokens.commands.CommandModule;
import net.thirdshift.tokens.commands.TokensCustomCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CacheTokensCommandModule extends CommandModule {
	public CacheTokensCommandModule(TokensCustomCommandExecutor executor) {
		super(executor);
		this.command="cache";
	}

	@Override
	public String getPermission() {
		return "tokens.cache";
	}

	@Override
	public String getDescription() {
		return "Used to edit cache settings";
	}

	@Override
	public String[] getCommandAliases() {
		return new String[0];
	}

	@Override
	public String getCommandUsage() {
		return "/tokens cache";
	}

	@Override
	public void onCommand(CommandSender commandSender, String[] args) {

		if ( args.length == 1 && args[0].equalsIgnoreCase("sync") ) {
			commandSender.sendMessage(plugin.messageHandler.formatMessage("tokens.cache.sync.start.message"));
			TokenCache.getInstance().submitAsyncSynchronizePlayers( commandSender );

		} else if ( args.length == 2 && args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("toggle") ) {
			TokenCache.getInstance().getStats().toggleEnabled();

			commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.toggled",
					TokenCache.getInstance().getStats().isEnabled()) );

		} else if ( args.length == 2 && args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("clear") ) {
			TokenCache.getInstance().getStats().clear();

			commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.cleared") );
		} else if ( args.length == 2 && args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("dump") ) {
			String playerCacheDump = TokenCache.getInstance().getPlayerDumpStats();
			commandSender.sendMessage( playerCacheDump );

		} else if ( args.length > 2 && args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("journaling") ) {
			TokenCache.getInstance().toggleJournal();
			if ( TokenCache.getInstance().isJournal() ) {
				commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.journaling-enabled",
						TokenCache.getInstance().isJournal()) );
				if ( args.length > 3 ) {
					TokenCache.getInstance().setJournalPlayer( args[3] );

					commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.journaling-player",
							TokenCache.getInstance().getJournalPlayer()) );
				}
			} else {
				commandSender.sendMessage( plugin.messageHandler.formatMessage("tokens.cache.stats.journaling-disabled",
						TokenCache.getInstance().getStats().isEnabled()) );
				TokenCache.getInstance().setJournalPlayer( null );
			}

		} else if ( args.length == 1 && args[0].equalsIgnoreCase("stats") ) {
			commandSender.sendMessage( TokenCache.getInstance().getStats().displayStats() );
			
		} else {
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
	}
}
