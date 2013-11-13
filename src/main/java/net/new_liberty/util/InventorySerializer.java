package net.new_liberty.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes inventories.
 */
public class InventorySerializer {
    /**
     * Private C'tor
     */
    private InventorySerializer() {
    }

    /**
     * Loads an inventory from a String into the given inventory.
     *
     * @param s Inventory string
     * @param inv Inventory to write to
     * @throws InvalidConfigurationException Corrupt inventory
     */
    public static void loadFromString(String s, Inventory inv) throws InvalidConfigurationException {
        YamlConfiguration chestFile = new YamlConfiguration();
        chestFile.loadFromString(s);

        for (int i = 0; i < inv.getSize(); i++) {
            Object o = chestFile.get(Integer.toString(i));
            if (o != null) {
                inv.setItem(i, (ItemStack) o);
            }
        }
    }

    /**
     * Writes an inventory to a string.
     *
     * @param inv The inventory to write.
     * @return
     */
    public static String writeToString(Inventory inv) {
        YamlConfiguration chestData = new YamlConfiguration();
        int slot = -1;
        for (ItemStack stack : inv.getContents()) {
            slot++;
            if (stack != null) {
                chestData.set(Integer.toString(slot), stack);
            }
        }
        return chestData.saveToString();
    }
}
