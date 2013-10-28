package net.new_liberty.votesuite;

import net.new_liberty.votesuite.command.VoteCommand;
import com.simplyian.easydb.EasyDB;
import java.util.*;
import java.util.logging.Level;
import net.new_liberty.core.module.Module;
import net.new_liberty.votesuite.command.VHomeCommand;
import net.new_liberty.votesuite.command.VSetHomeCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 * VoteSuite main class.
 */
public class VoteSuite extends Module {

    public static final List<String> CMDS = Arrays.asList(
            "money give %name% 12.5");

    public static final List<String> ALL_CMDS = Arrays.asList(
            "tell %name% Thank you for voting for New Liberty. You have been rewarded! Remember to vote again tomorrow!",
            "tell %name% You now regain health when you kill another player and break blocks faster for the next 24 hours.",
            "crent vote %name% 86400");

    public static final int VOTES_HOME = 3;

    private Map<String, VoteService> services;

    @Override
    public String[] getDependencies() {
        return new String[]{"EasyDB", "Votifier"};
    }

    @Override
    public void onEnable() {
        if (!EasyDB.getDb().isValid()) {
            getLogger().log(Level.WARNING, "EasyDB isn't connected; plugin loading halted.");
            return;
        }

        services = new HashMap<String, VoteService>();

        VoteService a = new VoteService("PlanetMinecraft.com", "Planet Minecraft");
        VoteService b = new VoteService("minecraftservers", "MinecraftServers.net");
        VoteService c = new VoteService("Minestatus", "Minestatus");
        VoteService d = new VoteService("MinecraftServers.org", "MinecraftServers.org");
        addServices(a, b, c, d);

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

        plugin.getCommand("vhome").setExecutor(new VHomeCommand(this));
        plugin.getCommand("vsethome").setExecutor(new VSetHomeCommand(this));
        plugin.getCommand("vote").setExecutor(new VoteCommand(this));
        Bukkit.getPluginManager().registerEvents(new VSListener(this), plugin);

        getLogger().log(Level.INFO, "Plugin loaded.");
    }

    private void addServices(VoteService... ss) {
        for (VoteService s : ss) {
            services.put(s.getId(), s);
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
