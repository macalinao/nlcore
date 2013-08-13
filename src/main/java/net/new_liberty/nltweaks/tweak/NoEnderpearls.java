package net.new_liberty.nltweaks.tweak;

import net.new_liberty.nltweaks.NLTweaks;
import net.new_liberty.nltweaks.Tweak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Disables the use of Ender Pearls.
 */
public class NoEnderpearls extends Tweak {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && !player.hasPermission("noenderpearls.bypass")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't use Ender Pearls on this server.");
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        }
    }

    @EventHandler
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof EnderPearl) {
            e.setCancelled(true);
        }
    }
}
