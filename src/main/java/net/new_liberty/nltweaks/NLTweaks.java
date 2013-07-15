package net.new_liberty.nltweaks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * This plugin contains all of the tweaks to New Liberty that don't warrant
 * their own plugin.
 */
public class NLTweaks extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    // Disable ender pearls
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && !player.hasPermission("noenderpearls.bypass")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't use Ender Pearls on this server.");
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        }
    }

    // Disable Strength II potions
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() != Material.POTION) {
            return;
        }

        Potion p = Potion.fromItemStack(e.getItem());
        if (p.getType() != PotionType.STRENGTH || p.getLevel() != 2) {
            return;
        }

        e.getPlayer().sendMessage(ChatColor.RED + "You can't use Strength II potions on this server.");
        e.setCancelled(true);
    }

    // Disable Strength II splash potions
    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        Potion p = Potion.fromItemStack(e.getPotion().getItem());
        if (p.getType() != PotionType.STRENGTH || p.getLevel() != 2) {
            return;
        }

        e.setCancelled(true);
    }
}
