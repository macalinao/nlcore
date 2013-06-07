package net.newliberty.enderchestprotect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderChestProtect extends JavaPlugin {
    private Map<String, Location> selectedChest = new HashMap();

    public Map<String, List<Location>> chestLocations = new HashMap<String, List<Location>>();

    public Map<String, Long> clearChests = new HashMap();

    public HashMap<String, Long> cooldowns = new HashMap();

    @Override
    public void onEnable() {
        getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        Bukkit.getPluginManager().registerEvents(new ECPListener(this), this);
        loadChestLocations();
    }

    public void loadChestLocations() {
        if (!getDataFolder().exists()) {
            return;
        }
        for (File file : getDataFolder().listFiles()) {
            FileConfiguration chestConfig = YamlConfiguration.loadConfiguration(file);
            World world = getServer().getWorld(file.getName().substring(file.getName().indexOf("CraftWorld{name=") + 16, file.getName().indexOf("},x=")));
            int x = Integer.parseInt(file.getName().substring(file.getName().indexOf("},x=") + 4, file.getName().indexOf(".0,y=")));
            int y = Integer.parseInt(file.getName().substring(file.getName().indexOf(",y=") + 3, file.getName().indexOf(".0,z=")));
            int z = Integer.parseInt(file.getName().substring(file.getName().indexOf(",z=") + 3, file.getName().indexOf(".0,pitch=")));
            if (chestConfig.getString("owner") != null) {
                addChestLocation(chestConfig.getString("owner"), new Location(world, x, y, z));
            }
        }
    }

    public void setSelectedChest(Player p, Location loc) {
        selectedChest.put(p.getName(), loc);
    }

    public Location getSelectedChest(String playerName) {
        if (selectedChest.containsKey(playerName)) {
            return (Location) selectedChest.get(playerName);
        }
        return null;
    }

    public void addChestLocation(String p, Location loc) {
        if (chestLocations.containsKey(p)) {
            chestLocations.get(p).add(loc);
        } else {
            chestLocations.put(p, new ArrayList());
            chestLocations.get(p).add(loc);
        }
    }

    public void removeChestLocation(String p, Location loc) {
        chestLocations.get(p).remove(chestLocations.get(p).indexOf(loc));
    }

    public int getChestCount(Player p) {
        if (chestLocations.containsKey(p.getName())) {
            return chestLocations.get(p.getName()).size();
        }
        return 0;
    }

    public void saveChestFile(Location loc, Inventory inv, String playerName) {
        File file = new File(getDataFolder(), loc.toString() + ".yml");
        FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create a new chest file for chest at " + loc.toString());
            }
        }


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

        if (getOwner(loc) == null) {
            chestFile.set("owner", playerName);
        }

        try {
            chestFile.save(file);
        } catch (IOException e) {
            getLogger().severe("Could not save chest file for chest at " + loc.toString());
        }
    }

    public Inventory loadChestData(Location loc, Inventory inv, String playerName) {
        File file = new File(getDataFolder(), loc.toString() + ".yml");
        FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            saveChestFile(loc, inv, playerName);
        } else if (chestFile.getConfigurationSection("inventory") != null) {
            for (int i = -1; i <= inv.getSize(); i++) {
                if (chestFile.get("inventory." + i) != null) {
                    inv.setItem(i, (ItemStack) chestFile.get("inventory." + i));
                }
            }
        }
        return inv;
    }

    public String getOwner(Location loc) {
        File file = new File(getDataFolder(), loc.toString() + ".yml");
        FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            return null;
        }
        return chestFile.getString("owner");
    }

    public int getAllowedChestCount(Player p) {
        for (int i = 15; i > 0; i--) {
            if (p.hasPermission("nlenderchest.place." + i)) {
                return i;
            }
        }
        return -1;
    }

    public boolean canPlaceChest(Player p) {
        if (getAllowedChestCount(p) == -1) {
            p.sendMessage(ChatColor.RED + "You are not allowed to place EnderChests");
            return false;
        }

        if (getChestCount(p) >= getAllowedChestCount(p)) {
            p.sendMessage(ChatColor.RED + "You have placed your maximum number of Protected EnderChests!");
            return false;
        }
        return true;
    }

    public boolean canBreakChest(Player p, Location loc) {
        File file = new File(getDataFolder(), loc.toString() + ".yml");
        FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);

        if (getOwner(loc) == null) {
            return true;
        }

        if ((!getOwner(loc).equals(p.getName())) && (!p.hasPermission("nlenderchest.admin"))) {
            p.sendMessage(ChatColor.BLUE + "This is not your Protected EnderChest. It belongs to " + ChatColor.GOLD + getOwner(loc));
            return false;
        }

        if (chestFile.getConfigurationSection("inventory") != null) {
            p.sendMessage(ChatColor.RED + "You cannot break this chest while there are items in it!");
            return false;
        }

        return true;
    }

    public boolean canOpenChest(Player p, Location loc) {
        if (getOwner(loc) == null) {
            p.sendMessage(ChatColor.RED + "This EnderChest belongs to no one, and cannot be opened.");
            return false;
        }

        if ((!getOwner(loc).equals(p.getName())) && (!p.hasPermission("nlenderchest.admin"))) {
            p.sendMessage(ChatColor.BLUE + "You cannot use this EnderChest, it belongs to " + ChatColor.GOLD + getOwner(loc));
            return false;
        }

        if (p.hasPermission("nlenderchest.admin")) {
            p.sendMessage(ChatColor.BLUE + "This EnderChest belongs to " + getOwner(loc));
        }

        return true;
    }
}
