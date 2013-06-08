package net.newliberty.enderchestprotect;

import java.util.HashMap;
import java.util.Map;
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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ECPListener implements Listener {
    private Map<String, EnderChest> selectedChest = new HashMap<String, EnderChest>();

    private EnderChestProtect plugin;

    public ECPListener(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestPlace(BlockPlaceEvent e) {
        if (!e.getBlock().getType().equals(Material.ENDER_CHEST)) {
            return;
        }

        Player p = e.getPlayer();

        if (plugin.getAllowedChestCount(p) == -1) {
            p.sendMessage(ChatColor.RED + "You are not allowed to place Ender Chests.");
            e.setCancelled(true);
            return;
        }

        if (plugin.getChestCount(p) >= plugin.getAllowedChestCount(p)) {
            p.sendMessage(ChatColor.RED + "You have placed your maximum number of protected Ender Chests!");
            e.setCancelled(true);
            return;
        }

        plugin.createChest(e.getPlayer().getName(), e.getBlock().getLocation());
        e.getPlayer().sendMessage(ChatColor.BLUE + "You have placed " + plugin.getChestCount(e.getPlayer()) + "/" + plugin.getAllowedChestCount(e.getPlayer()) + " protected Ender Chests.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.ENDER_CHEST) {
            return;
        }

        Location loc = e.getBlock().getLocation();

        EnderChest ec = plugin.getChest(loc);
        if (!ec.getOwner().equals(e.getPlayer().getName())) {
            return;
        }

        if (!ec.canBreak(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }

        if (ec.getOwner() == null) {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken a protected Ender Chest belonging to " + ChatColor.GOLD + "nobody" + ChatColor.BLUE + ". ?!??!?!?!");
        } else if (e.getPlayer().hasPermission("nlenderchest.admin")) {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken a protected Ender Chest belonging to " + ChatColor.GOLD + ec.getOwner() + ".");
        } else {
            e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken your protected Ender Chest.");
        }

        plugin.destroyChest(loc);
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

        EnderChest ec = plugin.getChest(block.getLocation());
        if (!ec.canOpen(e.getPlayer())) {
            return;
        }

        setSelectedChest(e.getPlayer(), ec);
        ec.open(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!e.getInventory().getTitle().equals("ProtectedEnderChest")) {
            return;
        }
        getSelectedChest(e.getPlayer()).save(e.getInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().getTitle().equals("ProtectedEnderChest")) {
            return;
        }
        getSelectedChest(e.getWhoClicked()).save(e.getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getPlayer().getInventory() == null) {
            return;
        }

        if (e.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("ProtectedEnderChest")) {
            getSelectedChest(e.getPlayer()).save(e.getPlayer().getOpenInventory().getTopInventory());
        }

        e.getPlayer().closeInventory();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        selectedChest.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        selectedChest.remove(e.getPlayer().getName());
    }

    private EnderChest getSelectedChest(HumanEntity player) {
        return selectedChest.get(player.getName());
    }

    private void setSelectedChest(Player p, EnderChest loc) {
        selectedChest.put(p.getName(), loc);
    }
}
