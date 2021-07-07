package net.thirdshift.tokens.hooks.bsp;

import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.prices.BSPriceTypeNumber;
import org.black_ixx.bossshop.core.rewards.BSRewardTypeNumber;
import org.black_ixx.bossshop.events.BSRegisterTypesEvent;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.black_ixx.bossshop.pointsystem.BSPointsPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;

public class BossShopHook extends TokensHook {

    public BossShopHook(Tokens tokens) {
        super(tokens);
        tokens.getServer().getPluginManager().registerEvents(new BSPListener(), tokens);
    }

    private class BSPListener implements Listener {
        @EventHandler
        public void register(BSRegisterTypesEvent event){
            new BSPTokensPoints().register();
            new BSPTokensReward().register();
            new BSPTokensPriceType().register();
            tokens.getLogger().info("Successfully hooked into BossShopPro");
        }
    }

    @Override
    public boolean consumesTokens() {
        return true;
    }

    private class BSPTokensReward extends BSRewardTypeNumber {

        @Override
        public boolean isIntegerValue() {
            return true;
        }

        @Override
        public void giveReward(Player p, BSBuy buy, Object reward, ClickType clickType, int multiplier) {
            int toAdd = (int) ClassManager.manager.getMultiplierHandler().calculateRewardWithMultiplier(p, buy, clickType, new Double(reward.toString()));
            tokensHandler.addTokens(p, toAdd);
        }

        @Override
        public Object createObject(Object o, boolean force_final_state) {
            return InputReader.getInt(o, -1);
        }

        @Override
        public boolean validityCheck(String item_name, Object o) {
            if ((Integer)o!=-1)
                return true;
            ClassManager.manager.getBugFinder().severe("Wasn't able to create ShopItem "+item_name+"! The reward needs to be a valid Integer.");
            return false;
        }

        @Override
        public void enableType() {
        }

        @Override
        public boolean canBuy(Player p, BSBuy buy, boolean message_if_no_success, Object reward, ClickType clickType) {
            return true;
        }

        @Override
        public String getDisplayReward(Player p, BSBuy buy, Object reward, ClickType clickType) {
            int numTokens = (int) ClassManager.manager.getMultiplierHandler().calculateRewardWithMultiplier(p, buy, clickType, new Double(reward.toString()));
            return numTokens+" Tokens";
        }

        @Override
        public String[] createNames() {
            return new String[]{"tokens", "token"};
        }

        @Override
        public boolean mightNeedShopUpdate() {
            return true;
        }
    }

    private class BSPTokensPriceType extends BSPriceTypeNumber {
        public BSPTokensPriceType(){
            updateNames();
        }

        @Override
        public boolean isIntegerValue() {
            return true;
        }

        @Override
        public String takePrice(Player p, BSBuy buy, Object price, ClickType clickType, int multiplier) {
            int numTokens = (int) ClassManager.manager.getMultiplierHandler().calculateRewardWithMultiplier(p, buy, clickType, new Double(price.toString()));
            tokensHandler.removeTokens(p, numTokens);
            return getDisplayBalance(p, buy, price, clickType);
        }

        @Override
        public boolean hasPrice(Player p, BSBuy buy, Object price, ClickType clickType, int multiplier, boolean messageOnFailure) {
            return tokensHandler.hasEnoughTokens(p, (int)price);
        }

        @Override
        public String getDisplayBalance(Player p, BSBuy buy, Object price, ClickType clickType) {
            return tokensHandler.getTokens(p)+" Tokens";
        }

        @Override
        public Object createObject(Object o, boolean force_final_state) {
            return InputReader.getInt(o, -1);
        }

        @Override
        public boolean validityCheck(String item_name, Object o) {
            if ((Integer)o!=-1)
                return true;
            ClassManager.manager.getBugFinder().severe("Wasn't able to create ShopItem "+item_name+"! The reward needs to be a valid Integer.");
            return false;
        }

        @Override
        public void enableType() {
        }

        @Override
        public String getDisplayPrice(Player p, BSBuy buy, Object price, ClickType clickType) {
            int numTokens = (int) ClassManager.manager.getMultiplierHandler().calculateRewardWithMultiplier(p, buy, clickType, new Double(price.toString()));
            return numTokens+" Tokens";
        }

        @Override
        public String[] createNames() {
            return new String[]{"tokens", "token"};
        }

        @Override
        public boolean mightNeedShopUpdate() {
            return true;
        }
    }

    private class BSPTokensPoints extends BSPointsPlugin {
        public BSPTokensPoints() {
            super("Tokens", "TOKENS", "tokens");
        }

        @Override
        public double getPoints(OfflinePlayer player) {
            return tokensHandler.getTokens(player.getPlayer());
        }

        @Override
        public double setPoints(OfflinePlayer player, double points) {
            tokensHandler.setTokens(player.getPlayer(), (int) points);
            return points;
        }

        @Override
        public double takePoints(OfflinePlayer player, double points) {
            tokensHandler.removeTokens(player.getPlayer(), (int) points);
            return getPoints(player);
        }

        @Override
        public double givePoints(OfflinePlayer player, double points) {
            tokensHandler.addTokens(player.getPlayer(), (int) points);
            return points;
        }

        @Override
        public boolean usesDoubleValues() {
            return false;
        }
    }
}
