package net.thirdshift.tokens.hooks;

import net.thirdshift.tokens.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class TokensHookRequirement {
    private boolean passed = false;
    protected Tokens tokens;

    public TokensHookRequirement(Tokens tokens, String requiredPluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(requiredPluginName);
        if (plugin != null && plugin.isEnabled()) {
            passed = true;
            this.tokens = tokens;
            initHook();
        }
    }

    public boolean hasPassed() {
        return passed;
    }

    public abstract TokensHook initHook();
}
