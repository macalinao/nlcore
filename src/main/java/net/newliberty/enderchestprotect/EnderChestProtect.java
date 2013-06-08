package net.newliberty.enderchestprotect;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderChestProtect extends JavaPlugin {
    public Map<String, List<Location>> chestLocations = new HashMap<String, List<Location>>();

    private Map<String, EnderChest> chests = new HashMap<String, EnderChest>();
    
    @Override
    public void onEnable() {
        getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        Bukkit.getPluginManager().registerEvents(new ECPListener(this), this);
        loadChestLocations();
    }

    /**
     * Loads all chests.
     */
    private void loadChestLocations() {
        if (!getDataFolder().exists()) {
            return;
        }
        for (File file : getDataFolder().listFiles()) {
            FileConfiguration chestConfig = YamlConfiguration.loadConfiguration(file);
            World world = getServer().getWorld(file.getName().substring(file.getName().indexOf("CraftWorld{name=") + 16, file.getName().indexOf("},x=")));
            int x = Integer.parseInt(file.getName().substring(file.getName().indexOf("},x=") + 4, file.getName().indexOf(".0,y=")));
            int y = Integer.parseInt(file.getName().substring(file.getName().indexOf(",y=") + 3, file.getName().indexOf(".0,z=")));
            int z = Integer.parseInt(file.getName().substring(file.getName().indexOf(",z=") + 3, file.getName().indexOf(".0,pitch=")));

            EnderChest e = loadChest(new Location(world, x, y, z));
            chests.put(e.getLocation().toString(), e);
        }
    }

    /**
     * Loads a chest from its location.
     *
     * @param loc
     * @return
     */
    private EnderChest loadChest(Location loc) {
        File file = getFile(loc);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(EnderChestProtect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);

        return new EnderChest(this, chestFile.getString("owner"), loc);
    }

    /**
     * Creates a chest with the given owner and location.
     *
     * @param owner
     * @param loc
     * @return
     */
    public EnderChest createChest(String owner, Location loc) {
        EnderChest ec = new EnderChest(this, owner, loc);
        chests.put(loc.toString(), ec);
        ec.save();
        return ec;
    }

    /**
     * Destroys a chest.
     *
     * @param loc
     */
    public void destroyChest(Location loc) {
        EnderChest ec = getChest(loc);
        chests.remove(loc.toString());
        ec.destroy();
    }

    /**
     * Gets the chest at the given location.
     *
     * @param loc
     * @return
     */
    public EnderChest getChest(Location loc) {
        return chests.get(loc.toString());
    }

    /**
     * Counts the number of chests the player owns.
     *
     * @param p
     * @return
     */
    public int getChestCount(Player p) {
        int count = 0;
        for (EnderChest chest : chests.values()) {
            count++;
        }
        return count;
    }

    /**
     * Gets the name of the chest file at the given location.
     *
     * @param loc
     * @return
     */
    public File getFile(Location loc) {
        return new File(getDataFolder(), loc.toString() + ".yml"); // VEHL PLS THIS SUCKS
    }

    public int getAllowedChestCount(Player p) {
        for (int i = 15; i > 0; i--) {
            if (p.hasPermission("nlenderchest.place." + i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if the given player can place more chests.
     *
     * @param p
     * @return
     */
    public boolean canPlaceChests(Player p) {
        if (getAllowedChestCount(p) == -1) {
            p.sendMessage(ChatColor.RED + "You are not allowed to place Ender Chests.");
            return false;
        }

        if (getChestCount(p) >= getAllowedChestCount(p)) {
            p.sendMessage(ChatColor.RED + "You have placed your maximum number of protected Ender Chests!");
            return false;
        }
        return true;
    }
}
