package net.thirdshift.tokens.hooks.picojobs;

import com.gmail.picono435.picojobs.api.EconomyImplementation;
import com.gmail.picono435.picojobs.api.PicoJobsAPI;
import net.thirdshift.tokens.Tokens;
import net.thirdshift.tokens.hooks.TokensHook;
import org.bukkit.entity.Player;

public class PicoHook extends TokensHook {
    public PicoHook(Tokens tokens) {
        super(tokens);
        if (PicoJobsAPI.registerEconomy(new TokensPicoEconomy())) {
            tokens.getLogger().info("Successfully registered into PicoJobs.");
        } else {
            tokens.getLogger().warning("Couldn't register into PicoJobs.");
        }
    }

    @Override
    public boolean consumesTokens() {
        return true;
    }

    private class TokensPicoEconomy extends EconomyImplementation {
        public TokensPicoEconomy() {
            this.requiredPlugin = tokens;
        }

        @Override
        public String getName() {
            return "Tokens";
        }

        @Override
        public double getBalance(Player player) {
            return tokensHandler.getTokens(player);
        }

        @Override
        public void deposit(Player player, double amount) {
            tokensHandler.addTokens(player, (int) amount);
        }

        @Override
        public void withdraw(Player player, double amount) {
            tokensHandler.removeTokens(player, (int) amount);
        }
    }
}
