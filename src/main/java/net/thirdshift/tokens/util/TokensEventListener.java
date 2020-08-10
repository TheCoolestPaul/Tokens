package net.thirdshift.tokens.util;

import net.thirdshift.tokens.Tokens;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TokensEventListener implements Listener {
	private String updateURL;
	private boolean outdated;
	private final Tokens plugin;

	public TokensEventListener(final Tokens plugin) {
		this.plugin=plugin;
		this.outdated = false;
	}

	public void setOutdated(boolean outdated) {
		this.outdated = outdated;
	}

	public boolean isOutdated() {
		return outdated;
	}

	public String getUpdateURL() {
		return updateURL;
	}

	public void setUpdateURL(String url) {
		this.updateURL = url;
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		if(isOutdated()) {
			Player player = event.getPlayer();
			if (player.isOp()) {
				player.sendMessage(ChatColor.GOLD+"[Tokens] "+ ChatColor.RED + "Tokens is outdated!");
				player.sendMessage(ChatColor.GOLD+"[Tokens] "+ ChatColor.WHITE + "An update is available at " + ChatColor.GREEN+ "" + updateURL);
			}
		}
	}
}
