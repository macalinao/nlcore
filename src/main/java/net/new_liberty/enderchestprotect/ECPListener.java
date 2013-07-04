package net.new_liberty.enderchestprotect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

public class ECPListener implements Listener {
    private EnderChestProtect plugin;

    public ECPListener(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!e.getBlock().getType().equals(Material.ENDER_CHEST)) {
            return;
        }

        Player p = e.getPlayer();
        String pn = p.getName();

        if (plugin.getAllowedChestCount(p) == -1) {
            p.sendMessage(ChatColor.RED + "You are not allowed to place Ender Chests.");
            e.setCancelled(true);
            return;
        }

        List<EnderChest> chests = plugin.getECManager().getChests(pn);
        if (chests.size() >= plugin.getAllowedChestCount(p)) {
            p.sendMessage(ChatColor.RED + "You have placed your maximum number of protected Ender Chests!");
            e.setCancelled(true);
            return;
        }

        plugin.getECManager().createChest(e.getPlayer().getName(), e.getBlock().getLocation());
        e.getPlayer().sendMessage(ChatColor.BLUE + "You have placed " + chests.size() + "/" + plugin.getAllowedChestCount(e.getPlayer()) + " protected Ender Chests.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.ENDER_CHEST) {
            return;
        }

        Location loc = e.getBlock().getLocation();

        Player p = e.getPlayer();
        EnderChest ec = plugin.getECManager().getChest(loc);

        // Check if we can access the Ender Chest
        boolean canAccess = ec.getOwner().equals(p.getName()) || p.hasPermission("nlenderchest.admin") || ec.isExpired();
        if (!canAccess) {
            p.sendMessage(ChatColor.RED + "You cannot access this Ender Chest as it belongs to " + ChatColor.GOLD + ec.getOwner());
            p.sendMessage(ec.getExpiryInfoMessage());
            e.setCancelled(true);
            return;
        }

        // Yes we can
        if (ec.hasItems()) {
            p.sendMessage(ChatColor.RED + "You cannot break this chest while there are items in it!");
            e.setCancelled(true);
            return;
        }

        if (ec.getOwner() == null) {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken a protected Ender Chest belonging to " + ChatColor.GOLD + "nobody" + ChatColor.BLUE + ". ?!??!?!?!");
        } else if (!e.getPlayer().getName().equals(ec.getOwner())) {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken a protected Ender Chest belonging to " + ChatColor.GOLD + ec.getOwner() + ".");
        } else {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken your protected Ender Chest.");
        }

        ec.destroy();
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Block block = e.getClickedBlock();
        if (!block.getType().equals(Material.ENDER_CHEST)) {
            return;
        }
        e.setCancelled(true);

        EnderChest ec = plugin.getECManager().getChest(block.getLocation());

        Player p = e.getPlayer();
        String owner = ec.getOwner();

        boolean canAccess = ec.getOwner().equals(p.getName()) || p.hasPermission("nlenderchest.admin") || ec.isExpired();
        if (!canAccess) {
            p.sendMessage(ChatColor.BLUE + "You cannot use this Ender Chest as it belongs to " + ChatColor.GOLD + owner + ".");
            p.sendMessage(ec.getExpiryInfoMessage());
            return;
        }

        if (p.hasPermission("nlenderchest.admin")) {
            p.sendMessage(ChatColor.BLUE + "This Ender Chest belongs to " + owner + ".");
        }

        if (e.getPlayer().getName().equals(ec.getOwner())) {
            ec.updateExpiryTime();
            p.sendMessage(ChatColor.YELLOW + "The protection on this chest has been renewed.");
        }
        ec.open(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        checkInventory(e.getInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        checkInventory(e.getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (p.getOpenInventory() == null) {
            return;
        }

        checkInventory(p.getOpenInventory().getTopInventory());
        p.closeInventory();
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        if (p.getOpenInventory() == null) {
            return;
        }

        checkInventory(p.getOpenInventory().getTopInventory());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (p.getOpenInventory() == null) {
            return;
        }

        checkInventory(p.getOpenInventory().getTopInventory());
    }

    /**
     * Saves the inventory if it was a protected Ender Chest.
     *
     * @param title
     * @param inv
     */
    private void checkInventory(Inventory inv) {
        String title = inv.getTitle();
        if (!title.startsWith("Ender Chest ")) {
            return;
        }
        int id = Integer.parseInt(title.substring("Ender Chest ".length()));
        EnderChest chest = plugin.getECManager().getChest(id);
        chest.save(inv);
    }
}
