package net.new_liberty.votesuite;

import com.simplyian.easydb.EasyDB;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * VoteSuite main class.
 */
public class VoteSuite extends JavaPlugin {
    private Map<String, VoteService> services;

    @Override
    public void onEnable() {
        if (!EasyDB.getDb().isValid()) {
            getLogger().log(Level.WARNING, "EasyDB isn't connected; plugin loading halted.");
            return;
        }

        loadConfig();

        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS votes ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "name varchar(16) NOT NULL,"
                + "service varchar(50) NOT NULL,"
                + "address varchar(50) NOT NULL,"
                + "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (id));");

        Bukkit.getPluginManager().registerEvents(new VSListener(this), this);

        getLogger().log(Level.INFO, "Plugin loaded.");
    }

    /**
     * Loads the config.
     */
    private void loadConfig() {
        // Load services
        services = new HashMap<String, VoteService>();
        ConfigurationSection servicesConfig = getConfig().getConfigurationSection("services");
        if (services == null) {
            servicesConfig = getConfig().createSection("services");
        }
        for (String serviceId : servicesConfig.getKeys(false)) {
            String svcName = servicesConfig.getString(serviceId + ".name", serviceId);
            String svcUrl = servicesConfig.getString(serviceId + ".url", "");
            VoteService service = new VoteService(serviceId, svcName, svcUrl);

            services.put(serviceId, service);
        }
    }

    /**
     * Gets the {@link VoteService} associated with the given id.
     *
     * @param serviceId
     * @return
     */
    public VoteService getService(String serviceId) {
        return services.get(serviceId);
    }
}
