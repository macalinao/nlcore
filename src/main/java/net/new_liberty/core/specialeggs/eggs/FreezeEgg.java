package net.new_liberty.core.specialeggs.eggs;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import java.util.HashMap;
import java.util.Map;
import net.new_liberty.core.specialeggs.ThrownEgg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Prevents people surrounding the egg's detonation location from moving.
 *
 * @author simplyianm
 */
public class FreezeEgg extends ThrownEgg {

    public static final int FREEZE_MS = 3000;

    public static final int FREEZE_IMMUNE_MS = 10000;

    public static final double FREEZE_RADIUS = 3.5;

    /**
     * Stores the times players were last frozen.
     */
    private Map<String, Long> lastFrozen = new HashMap<String, Long>();

    public FreezeEgg() {
        super("Freeze Egg");
        description = "Prevents enemies from moving.";
        eggType = EntityType.GHAST;
        allowInCombat = true;
        useInNoPvPZone = false;
        cooldown = 15;
        detonateTime = 3000;
    }

    @Override
    public boolean detonate(Player p, Location target) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (p == pl) {
                continue;
            }

            if (!pl.getLocation().getWorld().equals(target.getWorld())) {
                continue;
            }

            if (pl.getLocation().distanceSquared(target) > FREEZE_RADIUS * FREEZE_RADIUS && !isImmune(pl)) {
                continue;
            }

            FPlayer pp = FPlayers.i.get(p);
            FPlayer defender = FPlayers.i.get(pl);

            Relation r = pp.getRelationTo(defender);

            // You can never hurt faction members or allies
            if (r == Relation.MEMBER || r == Relation.ALLY) {
                continue;
            }

            freeze(pl);
        }

        target.getWorld().playEffect(target, Effect.SMOKE, 4);
        target.getWorld().playSound(target, Sound.GLASS, 1.0f, 0.5f);
        return true;
    }

    /**
     * Freezes a player.
     *
     * @param p
     */
    public void freeze(Player p) {
        lastFrozen.put(p.getName(), System.currentTimeMillis());
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

    /**
     * Checks if a player is immune to freezing.
     *
     * @param p
     * @return
     */
    public boolean isImmune(Player p) {
        Long last = lastFrozen.get(p.getName());
        return last != null && (last + FREEZE_IMMUNE_MS - System.currentTimeMillis()) > 0;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (isFrozen(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().setFallDistance(0f); // Prevent fall damage
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        if (!isFrozen((Player) e.getEntity())) {
            return;
        }

        if (e.getCause() == DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

}
