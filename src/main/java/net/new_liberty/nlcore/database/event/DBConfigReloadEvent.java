package net.new_liberty.nlcore.database.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when the database configuration is reloaded.
 */
public class DBConfigReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final boolean valid;

    public DBConfigReloadEvent(boolean valid) {
        this.valid = valid;
    }

    /**
     * Returns true if the new configuration is a valid database configuration.
     *
     * @return
     */
    public boolean isValid() {
        return valid;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
