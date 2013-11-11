package net.new_liberty.nlcore.module;

import java.io.File;
import java.util.logging.Logger;
import net.new_liberty.nlcore.NLCore;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;

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
     * Gets all the plugin dependencies of this module.
     *
     * @return
     */
    public String[] getDependencies() {
        return new String[0];
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
     * Adds a permission to this module.
     *
     * @param permission
     * @param description
     */
    protected final void addPermission(String permission, String description) {
        Permission p = new Permission(permission);
        p.setDescription(permission);
        plugin.getServer().getPluginManager().addPermission(p);
    }

    /**
     * Gets the logger of this tweak.
     *
     * @return
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets this module's data folder.
     *
     * @return
     */
    public File getDataFolder() {
        File f = new File(plugin.getDataFolder(), getName() + "/");
        f.mkdirs();
        return f;
    }

}
