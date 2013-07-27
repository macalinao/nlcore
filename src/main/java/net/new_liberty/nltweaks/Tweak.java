package net.new_liberty.nltweaks;

import org.bukkit.event.Listener;

/**
 * Represents a tweak in NL.
 */
public abstract class Tweak implements Listener {
    protected final NLTweaks plugin;

    /**
     * C'tor
     *
     * @param plugin
     */
    protected Tweak(NLTweaks plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when the tweak is enabled.
     */
    public void onEnable() {
    }
}
