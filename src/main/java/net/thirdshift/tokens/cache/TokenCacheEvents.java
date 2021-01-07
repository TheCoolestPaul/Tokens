package net.thirdshift.tokens.cache;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TokenCacheEvents
		implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	
    	Player player = event.getPlayer();
    	TokenCache.getInstance().submitAsyncLoadPlayer( player );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
    	
     	Player player = event.getPlayer();
    	TokenCache.getInstance().submitAsyncUnloadPlayer( player );
    }
}
