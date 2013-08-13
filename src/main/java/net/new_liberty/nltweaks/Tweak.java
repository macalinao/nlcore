package net.new_liberty.nltweaks;

import org.bukkit.event.Listener;

/**
 * Represents a tweak in NL.
 */
public abstract class Tweak implements Listener {
    protected NLTweaks plugin;

    private boolean initialized = false;

    /**
     * Initializes this tweak.
     *
     * @param plugin
     */
    public void initialize(NLTweaks plugin) {
        if (initialized) {
            return;
        }
        this.plugin = plugin;
        initialized = true;
    }

    /**
     * Called when the tweak is enabled.
     */
    public void onEnable() {
    }
}
