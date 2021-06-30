package net.thirdshift.tokens.bshop;

import net.thirdshift.tokens.TokensHandler;
import org.black_ixx.bossshop.pointsystem.BSPointsPlugin;
import org.bukkit.OfflinePlayer;

public class TokensBossShop extends BSPointsPlugin {
    private TokensHandler handler;

    public TokensBossShop(TokensHandler handler){
        this();
        this.handler = handler;
    }
    private TokensBossShop() {
        super("Tokens","TOKENS", "tokens");
    }

    @Override
    public double getPoints(OfflinePlayer player) {
        return handler.getTokens(player.getPlayer());
    }

    @Override
    public double setPoints(OfflinePlayer player, double points) {
        handler.setTokens(player.getPlayer(), (int)points);
        return points;
    }

    @Override
    public double takePoints(OfflinePlayer player, double points) {
        handler.removeTokens(player.getPlayer(), (int) points);
        return points;
    }

    @Override
    public double givePoints(OfflinePlayer player, double points) {
        handler.addTokens(player.getPlayer(), (int) points);
        return points;
    }

    @Override
    public boolean usesDoubleValues() {
        return false;
    }
}
