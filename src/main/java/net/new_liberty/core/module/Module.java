package net.new_liberty.core.module;

import java.util.logging.Logger;
import net.new_liberty.core.NLCore;
import org.bukkit.event.Listener;

/**
 * Represents a tweak in NL.
 */
public abstract class Module implements Listener {

    protected NLCore plugin;

    protected Logger logger;

    private boolean initialized = false;

    /**
     * Initializes this tweak.
     *
     * @param plugin
     */
    public void initialize(NLCore plugin) {
        if (initialized) {
            return;
        }
        this.plugin = plugin;
        logger = new ModuleLogger(this);
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
