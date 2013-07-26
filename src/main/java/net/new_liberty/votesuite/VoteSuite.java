package net.new_liberty.votesuite;

import net.new_liberty.votesuite.command.VoteCommand;
import com.simplyian.easydb.EasyDB;
import java.util.*;
import java.util.logging.Level;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
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

        saveDefaultConfig();
        loadConfig();

        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS votes ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "name varchar(16) NOT NULL,"
                + "service varchar(50) NOT NULL,"
                + "address varchar(50) NOT NULL,"
                + "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (id));");

        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS votes_recent ("
                + "name varchar(16) NOT NULL,"
                + "service varchar(50) NOT NULL,"
                + "PRIMARY KEY (name, service));");

        getCommand("vote").setExecutor(new VoteCommand(this));
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
            String svcId = servicesConfig.getString(serviceId + ".id", serviceId);
            String svcName = servicesConfig.getString(serviceId + ".name", serviceId);
            VoteService service = new VoteService(svcId, svcName);

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

    /**
     * Gets a set of our services.
     *
     * @return
     */
    public Set<VoteService> getServices() {
        return new HashSet<VoteService>(services.values());
    }

    /**
     * Gets the services a player hasn't voted for recently.
     *
     * @param player
     * @return
     */
    public Set<VoteService> getMissingServices(String player) {
        List<String> voteSvcIds = EasyDB.getDb().query("SELECT service FROM votes_recent WHERE name = ?", new ColumnListHandler<String>(), player);
        Set<VoteService> missingServices = getServices();
        for (String voteSvcId : voteSvcIds) {
            VoteService rm = null;
            for (VoteService svc : missingServices) {
                if (svc.getId().equals(voteSvcId)) {
                    rm = svc;
                    break;
                }
            }
            missingServices.remove(rm);
        }
        return missingServices;
    }
}
