package net.new_liberty.enderchestprotect;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

        if (plugin.getAllowedChestCount(p) == 0) {
            p.sendMessage(ChatColor.RED + "You are not allowed to place Ender Chests.");
            e.setCancelled(true);
            return;
        }

        int count = plugin.getECManager().getChestCount(pn);
        if (count >= plugin.getAllowedChestCount(p)) {
            p.sendMessage(ChatColor.RED + "You have placed your maximum number of protected Ender Chests!");
            e.setCancelled(true);
            return;
        }

        plugin.getECManager().createChest(e.getPlayer().getName(), e.getBlock().getLocation());
        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have placed " + ChatColor.AQUA
                + (count + 1) + "/" + plugin.getAllowedChestCount(e.getPlayer()) + ChatColor.YELLOW + " protected Ender Chests.");
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
        if (!ec.canAccess(p)) {
            p.sendMessage(ChatColor.RED + "You cannot break this Ender Chest as it belongs to " + ChatColor.GOLD + ec.getOwner());
            p.sendMessage(ec.getExpiryInfoMessage());
            e.setCancelled(true);
            return;
        }

        // Yes we can, drop items if there are some
        if (ec.hasItems()) {
            Inventory inv = ec.getInventory();
            for (ItemStack i : inv.getContents()) {
                if (i == null) {
                    continue;
                }
                ec.getLocation().getWorld().dropItemNaturally(ec.getLocation(), i);
            }
        }

        if (!p.getName().equals(ec.getOwner())) {
            p.sendMessage(ChatColor.YELLOW + "You have broken a protected Ender Chest belonging to " + ChatColor.AQUA + ec.getOwner() + ChatColor.YELLOW + ".");
        } else {
            p.sendMessage(ChatColor.YELLOW + "You have broken your protected Ender Chest.");
        }

        ec.destroy();
    }

    @EventHandler
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

        if (!ec.canAccess(p)) {
            p.sendMessage(ChatColor.BLUE + "You cannot access this Ender Chest as it belongs to " + ChatColor.GOLD + owner + ".");
            p.sendMessage(ec.getExpiryInfoMessage());
            return;
        }

        if (p.getName().equals(ec.getOwner())) {
            ec.updateExpiryTime();
            p.sendMessage(ChatColor.YELLOW + "The protection on this chest has been renewed to expire on " + ChatColor.AQUA + ec.getExpiryTimeString() + ChatColor.YELLOW + ".");
        } else {
            p.sendMessage(ChatColor.BLUE + "This Ender Chest belongs to " + owner + ".");
        }

        p.openInventory(ec.getInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        String title = inv.getTitle();
        if (!title.startsWith("Ender Chest ")) {
            return;
        }
        int id = Integer.parseInt(title.substring("Ender Chest ".length()));
        EnderChest chest = plugin.getECManager().getChest(id);
        chest.save();
    }
}
