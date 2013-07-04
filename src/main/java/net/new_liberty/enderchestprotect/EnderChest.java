package net.new_liberty.enderchestprotect;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author simplyianm
 */
public class EnderChest {
    private EnderChestProtect plugin;

    private String owner;

    private Location loc;

    public EnderChest(EnderChestProtect plugin, String owner, Location loc) {
        this.plugin = plugin;
        this.owner = owner;
        this.loc = loc;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String name) {
        owner = name;
    }

    public Location getLocation() {
        return loc;
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(p, 27, "ProtectedEnderChest");

        FileConfiguration chestFile = YamlConfiguration.loadConfiguration(plugin.getFile(loc));
        if (chestFile.getConfigurationSection("inventory") != null) {
            for (int i = 0; i < inv.getSize(); i++) {
                if (chestFile.get("inventory." + i) != null) {
                    inv.setItem(i, (ItemStack) chestFile.get("inventory." + i));
                }
            }
        }

        p.openInventory(inv);
    }

    /**
     * Checks if this Ender Chest contains items.
     *
     * @return
     */
    public boolean hasItems() {
        FileConfiguration chestFile = YamlConfiguration.loadConfiguration(plugin.getFile(loc));
        return chestFile.getConfigurationSection("inventory") != null;
    }

    /**
     * Saves the file.
     */
    public void save() {
        save(null);
    }

    /**
     * Saves the file with the given inventory.
     *
     * @param inv
     */
    public void save(Inventory inv) {
        File file = plugin.getFile(loc);
        try {
            file.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create a new chest file for chest at " + loc.toString());
        }

        FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);

        if (inv != null) {
            int slot = -1;
            chestFile.set("inventory", null);
            for (ItemStack stack : inv.getContents()) {
                slot++;
                if (stack != null) {
                    chestFile.set("inventory." + slot, stack);
                }
            }
        }

        if (owner != null) {
            chestFile.set("owner", owner);
        }

        try {
            chestFile.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save chest file for chest at " + loc.toString());
        }
    }

    /**
     * Destroys the Ender Chest by destroying its file.
     */
    public void destroy() {
        plugin.getFile(loc).delete();
        Block b = loc.getBlock();
        if (b != null) {
            b.setType(Material.AIR);
        }
    }
}
