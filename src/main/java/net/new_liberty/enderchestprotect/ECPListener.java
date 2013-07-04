package net.new_liberty.enderchestprotect;

import java.util.HashMap;
import java.util.List;
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
    private Map<String, EnderChest> selectedChests = new HashMap<String, EnderChest>();

    private Map<String, Long> cooldowns = new HashMap<String, Long>();

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

        List<EnderChest> chests = plugin.getChests(pn);
        if (chests.size() >= plugin.getAllowedChestCount(p)) {
            p.sendMessage(ChatColor.RED + "You have placed your maximum number of protected Ender Chests!");
            e.setCancelled(true);
            return;
        }

        plugin.createChest(e.getPlayer().getName(), e.getBlock().getLocation());
        e.getPlayer().sendMessage(ChatColor.BLUE + "You have placed " + chests.size() + "/" + plugin.getAllowedChestCount(e.getPlayer()) + " protected Ender Chests.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.ENDER_CHEST) {
            return;
        }

        Location loc = e.getBlock().getLocation();

        Player p = e.getPlayer();
        EnderChest ec = plugin.getChest(loc);

        if (ec.getOwner() != null) {
            if (!ec.getOwner().equals(p.getName()) && !p.hasPermission("nlenderchest.admin")) {
                p.sendMessage(ChatColor.BLUE + "This is not your protected Ender Chest. It belongs to " + ChatColor.GOLD + ec.getOwner());
                e.setCancelled(true);
                return;
            }

            if (ec.hasItems()) {
                p.sendMessage(ChatColor.RED + "You cannot break this chest while there are items in it!");
                e.setCancelled(true);
                return;
            }
        } else {
            p.sendMessage(ChatColor.RED + "This Ender Chest belongs to no one.");
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

        // Wait 2 seconds before opening another chest
        int cooldown = 2000;
        if ((cooldowns.containsKey(e.getPlayer().getName())) && (cooldowns.get(e.getPlayer().getName()) + cooldown - System.currentTimeMillis() > 0L)) {
            return;
        }
        cooldowns.put(e.getPlayer().getName(), Long.valueOf(System.currentTimeMillis()));

        EnderChest ec = plugin.getChest(block.getLocation());

        Player p = e.getPlayer();
        String owner = ec.getOwner();
        if (owner == null) {
            p.sendMessage(ChatColor.RED + "This Ender Chest belongs to no one and cannot be opened.");
            return;
        }

        if (!owner.equals(p.getName()) && !p.hasPermission("nlenderchest.admin")) {
            p.sendMessage(ChatColor.BLUE + "You cannot use this Ender Chest as it belongs to " + ChatColor.GOLD + owner + ".");
            return;
        }

        if (p.hasPermission("nlenderchest.admin")) {
            p.sendMessage(ChatColor.BLUE + "This Ender Chest belongs to " + owner + ".");
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
        selectedChests.remove(e.getPlayer().getName());
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
        Player p = e.getPlayer();
        if (p.getOpenInventory() == null) {
            return;
        }

        if (p.getOpenInventory().getTitle().equalsIgnoreCase("ProtectedEnderChest")) {
            getSelectedChest(p).save(p.getOpenInventory().getTopInventory());
        }

        p.closeInventory();
        selectedChests.remove(p.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        selectedChests.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        selectedChests.remove(e.getPlayer().getName());
    }

    private EnderChest getSelectedChest(HumanEntity player) {
        return selectedChests.get(player.getName());
    }

    private void setSelectedChest(Player p, EnderChest loc) {
        selectedChests.put(p.getName(), loc);
    }
}
