package net.new_liberty.core.enderchestprotect;

import com.simplyian.easydb.EasyDB;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.new_liberty.core.module.Module;
import net.new_liberty.core.enderchestprotect.command.ECClearCommand;
import net.new_liberty.core.enderchestprotect.command.ECConfirmCommand;
import net.new_liberty.core.enderchestprotect.command.ECListCommand;
import net.new_liberty.core.enderchestprotect.command.ECViewCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EnderChestProtect extends Module {

    private Map<String, ClearChestTimer> clearChests = new HashMap<String, ClearChestTimer>();

    private ECManager ecManager;

    @Override
    public String[] getDependencies() {
        return new String[]{"EasyDB"};
    }

    @Override
    public void onEnable() {
        if (!EasyDB.getDb().isValid()) {
            getLogger().log(Level.SEVERE, "Invalid database credentials; plugin loading halted.");
            return;
        }

        ecManager = new ECManager(this);

        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS enderchests ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "owner VARCHAR(16) NOT NULL,"
                + "world VARCHAR(255) NOT NULL,"
                + "x INT(10) NOT NULL,"
                + "y INT(10) NOT NULL,"
                + "z INT(10) NOT NULL,"
                + "contents TEXT,"
                + "access_time TIMESTAMP NOT NULL,"
                + "PRIMARY KEY (id));");

        plugin.getCommand("ecclear").setExecutor(new ECClearCommand(this));
        plugin.getCommand("ecconfirm").setExecutor(new ECConfirmCommand(this));
        plugin.getCommand("eclist").setExecutor(new ECListCommand(this));
        plugin.getCommand("ecview").setExecutor(new ECViewCommand(this));

        Bukkit.getPluginManager().registerEvents(new ECPListener(this), plugin);

        getLogger().log(Level.INFO, "Plugin enabled.");
    }

    /**
     * Gets the Ender Chest manager.
     *
     * @return
     */
    public ECManager getECManager() {
        return ecManager;
    }

    /**
     * Gets the time of new expiry.
     *
     * @return
     */
    public int getExpiryMillis() {
        return 14 * 24 * 60 * 60 * 1000; // 14 days
    }

    public Map<String, ClearChestTimer> getClearChests() {
        return clearChests;
    }

    /**
     * Gets the amount of chests a given player is allowed to have.
     *
     * @param p
     * @return
     */
    public int getAllowedChestCount(Player p) {
        for (int i = 15; i > 0; i--) {
            if (p.hasPermission("nlenderchest.place." + i)) {
                return i;
            }
        }
        return 0;
    }

}
