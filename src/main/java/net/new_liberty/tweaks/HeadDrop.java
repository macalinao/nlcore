/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.tweaks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import net.new_liberty.core.module.Module;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author simplyianm
 */
public class HeadDrop extends Module {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy hh:mm aaa");

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!(player.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        Entity cause = ((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager();
        Player killer;

        if (cause instanceof Player) {
            killer = (Player) cause;

        } else if (cause instanceof Projectile) {
            Projectile proj = (Projectile) cause;
            Entity shooter = proj.getShooter();
            if (!(shooter instanceof Player)) {
                return;
            }
            killer = (Player) shooter;

        } else {
            return;
        }


        Location loc = player.getLocation();

        // Skull handling
        double chance = 0.1; // 10% chance of dropping a head
        if (chance > Math.random()) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta sm = (SkullMeta) skull.getItemMeta();
            sm.setOwner(player.getName());
            sm.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.WHITE + "Killed by " + ChatColor.AQUA + killer.getName() + ChatColor.WHITE + " on " + ChatColor.YELLOW + DATE_FORMAT.format(new Date())));
            skull.setItemMeta(sm);
            loc.getWorld().dropItemNaturally(loc, skull);
        }
    }

}
