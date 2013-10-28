package net.new_liberty.tweaks;

import net.new_liberty.core.NLCore;
import net.new_liberty.core.module.Module;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Prevents players from using invisibility potions. The current plugin likes to
 * error.
 */
public class NoInvisibilityPots extends Module {
    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        Potion p;
        try {
            p = Potion.fromItemStack(e.getPotion().getItem());
        } catch (IllegalArgumentException ex) {
            return;
        }

        if (p.getType() == PotionType.INVISIBILITY) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() != Material.POTION) {
            return;
        }

        Potion p;
        try {
            p = Potion.fromItemStack(e.getItem());
        } catch (IllegalArgumentException ex) {
            return;
        }

        if (p.getType() != PotionType.INVISIBILITY) {
            return;
        }

        e.getPlayer().sendMessage(ChatColor.RED + "Invisibility potions are not available on this server.");
        e.setCancelled(true);
    }
}
