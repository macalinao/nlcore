package net.new_liberty.nltweaks.tweak.specialeggs.eggs;

import net.new_liberty.nltweaks.tweak.specialeggs.ThrownEgg;
import org.bukkit.Location;
import org.bukkit.Sound;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Prevents people surrounding the egg's detonation location from moving.
 *
 * @author simplyianm
 */
public class FreezeEgg extends ThrownEgg {

    public FreezeEgg() {
        super("Freeze Egg");
        description = "Prevents people surrounding the egg's detonation location from moving.";
        eggType = EntityType.ENDERMAN;
        allowInCombat = false;
        useInNoPvPZone = false;
        cooldown = 60;
        detonateTime = 3000;
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
