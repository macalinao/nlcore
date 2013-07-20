package net.new_liberty.nltweaks;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

    // Disable placing of TNT minecarts
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            return;
        }

        if (e.getItem().getType() == Material.EXPLOSIVE_MINECART) {
            e.getPlayer().sendMessage(ChatColor.RED + "TNT Minecarts are currently disabled due to a bug in Minecraft.");
            e.setCancelled(true);
        }
    }

    // Nerf Strength pots
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getDamager();
        int level = -1;

        for (PotionEffect f : p.getActivePotionEffects()) {
            if (f.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                level = f.getAmplifier();
                break;
            }
        }

        if (level == -1) {
            return;
        }

        double newDmg = e.getDamage();
        if (level == 0) { // Strength I -- +130%
            newDmg /= 2.3;
            newDmg += 3;

        } else if (level == 1) {  // Strength II -- +260%
            newDmg /= 3.6;
            newDmg += 6;
        }

        e.setDamage(newDmg);
    }
}
