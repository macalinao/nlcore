package net.nl.ecp;

import java.io.File;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class ChestListener
        implements Listener {
    private EnderChestProtect plugin;

    public ChestListener(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() != Material.ENDER_CHEST) {
            return;
        }
        if (e.isCancelled()) {
            return;
        }
        if (!plugin.canPlaceChest(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }
        plugin.addChestLocation(e.getPlayer().getName(), e.getBlock().getLocation());
        e.getPlayer().sendMessage(ChatColor.BLUE + "You have placed " + plugin.getChestCount(e.getPlayer()) + "/" + plugin.getAllowedChestCount(e.getPlayer()) + " Protected EnderChests");
        plugin.saveChestFile(e.getBlock().getLocation(), null, e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.ENDER_CHEST) {
            return;
        }
        if ((e.isCancelled()) && (!plugin.getOwner(e.getBlock().getLocation()).equalsIgnoreCase(e.getPlayer().getName()))) {
            return;
        }
        if (!plugin.canBreakChest(e.getPlayer(), e.getBlock().getLocation())) {
            e.setCancelled(true);
            return;
        }

        if (plugin.getOwner(e.getBlock().getLocation()) == null) {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken a ProtectedEnderChest belonging to " + ChatColor.GOLD + "nobody");
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            File file = new File(plugin.getDataFolder(), e.getBlock().getLocation().toString() + ".yml");
            if (file.exists()) {
                file.delete();
            }
            return;
        }
        if (e.getPlayer().hasPermission("nlenderchest.admin")) {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken a ProtectedEnderChest belonging to " + ChatColor.GOLD + plugin.getOwner(e.getBlock().getLocation()));
        } else {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken your Protected EnderChest");
        }

        plugin.removeChestLocation(plugin.getOwner(e.getBlock().getLocation()), e.getBlock().getLocation());
        File file = new File(plugin.getDataFolder(), e.getBlock().getLocation().toString() + ".yml");
        file.delete();
        e.setCancelled(true);
        e.getBlock().setType(Material.AIR);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestOpen(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = e.getClickedBlock();
        if (block.getType() != Material.ENDER_CHEST) {
            return;
        }
        e.setCancelled(true);

        int cooldown = 2000;
        if ((plugin.cooldowns.containsKey(e.getPlayer().getName())) && (((Long) plugin.cooldowns.get(e.getPlayer().getName())).longValue() + cooldown - System.currentTimeMillis() > 0L)) {
            return;
        }
        plugin.cooldowns.put(e.getPlayer().getName(), Long.valueOf(System.currentTimeMillis()));

        File file = new File(plugin.getDataFolder(), block.getLocation().toString() + ".yml");

        if (file.exists()) {
            if (!plugin.canOpenChest(e.getPlayer(), block.getLocation())) {
                return;
            }
            openChestInventory(block.getLocation(), e.getPlayer());
        }
    }

    @EventHandler
    public void onChestClose(InventoryCloseEvent e) {
        if (!e.getInventory().getTitle().equals("ProtectedEnderChest")) {
            return;
        }
        plugin.saveChestFile(plugin.getSelectedChest(e.getPlayer().getName()), e.getInventory(), e.getPlayer().getName());
    }

    @EventHandler
    public void onChestInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().getTitle().equals("ProtectedEnderChest")) {
            return;
        }
        plugin.saveChestFile(plugin.getSelectedChest(e.getWhoClicked().getName()), e.getInventory(), e.getWhoClicked().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getPlayer().getInventory() == null) {
            return;
        }
        if (e.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("ProtectedEnderChest")) {
            plugin.saveChestFile(plugin.getSelectedChest(e.getPlayer().getName()), e.getPlayer().getOpenInventory().getTopInventory(), e.getPlayer().getName());
        }
        e.getPlayer().closeInventory();
    }

    public void openChestInventory(Location loc, Player p) {
        plugin.setSelectedChest(p, loc);

        Inventory inventory = Bukkit.createInventory(p, 27, "ProtectedEnderChest");
        inventory = plugin.loadChestData(loc, inventory, p.getName());
        p.openInventory(inventory);
    }
}

/* Location:           /Users/simplyianm/Desktop/EnderChestProtect.jar
 * Qualified Name:     net.nl.ecp.ChestListener
 * JD-Core Version:    0.6.2
 */