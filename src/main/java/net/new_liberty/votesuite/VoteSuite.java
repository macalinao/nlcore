package net.new_liberty.votesuite;

import net.new_liberty.votesuite.command.VoteCommand;
import com.simplyian.easydb.EasyDB;
import java.util.*;
import java.util.logging.Level;
import net.new_liberty.votesuite.command.VHomeCommand;
import net.new_liberty.votesuite.command.VSetHomeCommand;
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

        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS votes_homes ("
                + "name varchar(16) NOT NULL,"
                + "world varchar(32) NOT NULL,"
                + "x DOUBLE NOT NULL,"
                + "y DOUBLE NOT NULL,"
                + "z DOUBLE NOT NULL,"
                + "yaw FLOAT NOT NULL,"
                + "pitch FLOAT NOT NULL,"
                + "PRIMARY KEY (name));");

        getCommand("vhome").setExecutor(new VHomeCommand(this));
        getCommand("vsethome").setExecutor(new VSetHomeCommand(this));
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
        for (String key : servicesConfig.getKeys(false)) {
            String svcId = servicesConfig.getString(key + ".id", key);
            String svcName = servicesConfig.getString(key + ".name", key);
            VoteService service = new VoteService(svcId, svcName);

            services.put(svcId, service);
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
     * Gets a Voter.
     *
     * @param player
     * @return
     */
    public Voter getVoter(String player) {
        return new Voter(this, player);
    }
}
