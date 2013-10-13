package net.new_liberty.nltweaks;

import java.util.logging.Logger;
import org.bukkit.event.Listener;

/**
 * Represents a tweak in NL.
 */
public abstract class Tweak implements Listener {

    protected NLTweaks plugin;

    protected Logger logger;

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
        logger = new TweakLogger(this);
        initialized = true;
    }

    /**
     * Called when the tweak is enabled.
     */
    public void onEnable() {
    }

    /**
     * Called when the tweak is disabled.
     */
    public void onDisable() {
    }

    /**
     * Gets the name of this tweak.
     *
     * @return
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Gets the logger of this tweak.
     *
     * @return
     */
    public Logger getLogger() {
        return logger;
    }

}
