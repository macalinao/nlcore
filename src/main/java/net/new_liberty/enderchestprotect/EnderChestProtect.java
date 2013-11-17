package net.new_liberty.enderchestprotect;

import net.new_liberty.nlcore.database.EasyDB;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.new_liberty.nlcore.module.Module;
import net.new_liberty.enderchestprotect.command.ECClearCommand;
import net.new_liberty.enderchestprotect.command.ECConfirmCommand;
import net.new_liberty.enderchestprotect.command.ECListCommand;
import net.new_liberty.enderchestprotect.command.ECViewCommand;
import net.new_liberty.nlcore.player.DonorRank;
import net.new_liberty.nlcore.player.NLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EnderChestProtect extends Module {

    public static int EXPIRY_MINUTES = 14 * 24 * 60;

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

        addPermission("ecp.admin", "EnderChestProtect admin permission");

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
        return EXPIRY_MINUTES * 60 * 1000; // 14 days
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
        if (p.hasPermission("ncp.admin")) {
            return 1000;
        }

        NLPlayer n = new NLPlayer(p);
        DonorRank r = n.getDonorRank();
        switch (r) {
            case PREMIUM:
                return 1;
            case HERO:
                return 2;
            case ELITE:
                return 3;
            case GUARDIAN:
                return 4;
            case CHAMPION:
                return 5;
        }
        return 0;
    }

}
