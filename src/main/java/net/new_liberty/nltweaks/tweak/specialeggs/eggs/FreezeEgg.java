package net.new_liberty.nltweaks.tweak.specialeggs.eggs;

import java.util.HashMap;
import java.util.Map;
import net.new_liberty.nltweaks.tweak.specialeggs.ThrownEgg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Prevents people surrounding the egg's detonation location from moving.
 *
 * @author simplyianm
 */
public class FreezeEgg extends ThrownEgg {

    public static final int FREEZE_MS = 3000;

    public static final double FREEZE_RADIUS = 3.5;

    /**
     * Stores the times players were last frozen.
     */
    private Map<String, Long> lastFrozen = new HashMap<String, Long>();

    public FreezeEgg() {
        super("Freeze Egg");
        description = "Prevents enemies from moving.";
        eggType = EntityType.GHAST;
        allowInCombat = false;
        useInNoPvPZone = false;
        cooldown = 60;
        detonateTime = 3000;
    }

    @Override
    public boolean detonate(Player p, Location target) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.getLocation().distanceSquared(target) < FREEZE_RADIUS * FREEZE_RADIUS) {
                // Freeze them
                freeze(pl);
            }
        }
        return true;
    }

    /**
     * Freezes a player.
     *
     * @param p
     */
    public void freeze(Player p) {
        Location l = p.getLocation();
        lastFrozen.put(p.getName(), System.currentTimeMillis());
        p.getWorld().playEffect(l, Effect.SMOKE, 4);
        p.getWorld().playSound(l, Sound.GLASS, 1.0f, 0.5f);
        p.sendMessage(ChatColor.AQUA + "You've been frozen by a freeze egg!");
    }

    /**
     * Checks if a player is frozen.
     *
     * @param p
     * @return
     */
    public boolean isFrozen(Player p) {
        Long last = lastFrozen.get(p.getName());
        return last != null && (last + FREEZE_MS - System.currentTimeMillis()) > 0;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
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
