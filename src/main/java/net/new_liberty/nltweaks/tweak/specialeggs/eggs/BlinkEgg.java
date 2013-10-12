package net.new_liberty.nltweaks.tweak.specialeggs.eggs;

import net.new_liberty.nltweaks.tweak.specialeggs.ThrownEgg;
import org.bukkit.Location;
import org.bukkit.Sound;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Teleports the player a short distance (wherever this egg lands).
 *
 * @author simplyianm
 */
public class BlinkEgg extends ThrownEgg {

    public BlinkEgg() {
        super("Blink Egg");
        description = "Teleports you a short distance.";
        eggType = EntityType.ENDERMAN;
        allowInCombat = false;
        useInNoPvPZone = false;
        cooldown = 10;
        detonateTime = 3000;
    }

    /**
     * Handles fall damage. You're immune to it while this egg is on its 10 sec
     * cooldown.
     *
     * @param e
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();
        if (e.getCause() != DamageCause.FALL) {
            return;
        }

        // Check if the egg is still on cooldown.
        if (getCd(p) != 0) {
            e.setCancelled(true);
        }
    }

    @Override
    public boolean detonate(Player p, Location target) {
        p.teleport(target);
        p.getWorld().playSound(p.getLocation(), Sound.WITHER_SHOOT, 1.0f, 1.0f);
        return true;
    }

    @EventHandler
    @Override
    public void onEggUse(PlayerInteractEvent e) {
        super.onEggUse(e);
    }

    @EventHandler
    @Override
    public void onEggThrow(PlayerEggThrowEvent e) {
        super.onEggThrow(e);
    }

    @EventHandler
    @Override
    public void onEggHit(ProjectileHitEvent e) {
        super.onEggHit(e);
    }

}
