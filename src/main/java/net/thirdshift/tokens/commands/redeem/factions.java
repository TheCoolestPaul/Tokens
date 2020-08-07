package net.thirdshift.tokens.commands.redeem;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class factions {
    public static void redeemFactions(Player player, int toRedeem, Tokens plugin){
        List<Object> objects = new ArrayList<>();
        objects.add(toRedeem);
        objects.add(new PlayerSender(player));
        if (plugin.getHandler().hasTokens(player, toRedeem)) {
            FPlayer facPly = FPlayers.getInstance().getByPlayer(player);
            if (facPly != null) {
                facPly.setPowerBoost(facPly.getPowerBoost() + (double)(toRedeem * plugin.tokenToFactionPower));
                objects.add(facPly);
                plugin.getHandler().setTokens(player, plugin.getHandler().getTokens(player) - toRedeem);
                player.sendMessage(plugin.messageHandler.useMessage("redeem.factions", objects));
            }else{
                plugin.getLogger().severe("Couldn't get FPlayer for "+player.getName());
            }
        } else {
            player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", objects)); }
    }
}
