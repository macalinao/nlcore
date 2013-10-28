package net.new_liberty.core.specialeggs;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import java.util.ArrayList;
import java.util.List;
import net.new_liberty.core.NLCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents an egg that is thrown. This sets up a timer for the thrown egg.
 */
public abstract class ThrownEgg extends SpecialEgg {

    /**
     * The time the egg should detonate in milliseconds.
     */
    protected int detonateTime = 3000;

    private List<ThrownEggTimer> timers = new ArrayList<ThrownEggTimer>();

    protected ThrownEgg(String name) {
        super(name);
    }

    @Override
    public void onEnable() {
        (new BukkitRunnable() {
            @Override
            public void run() {
                List<ThrownEggTimer> rem = new ArrayList<ThrownEggTimer>();
                for (ThrownEggTimer t : timers) {
                    if (t.check()) {
                        rem.add(t);
                    }
                }

                for (ThrownEggTimer r : rem) {
                    timers.remove(r);
                }
            }

        }).runTaskTimer(NLCore.getInstance(), 2, 2);
    }

    /**
     * Called when the egg is detonated.
     *
     * @param p
     * @param target
     * @return
     */
    public abstract boolean detonate(Player p, Location target);

    /**
     * Override this method, add an annotation, and call super.
     *
     * @param e
     */
    @EventHandler
    public void onEggUse(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                || !e.hasItem()) {
            return;
        }

        if (!isInstance(e.getItem())) {
            return;
        }

        e.setCancelled(true);
        if (!checkCanUse(e.getPlayer())) {
            return;
        }

        Player p = e.getPlayer();
        timers.add(new ThrownEggTimer(p.getName(), p.launchProjectile(Egg.class)));

        int amt = e.getItem().getAmount();
        if (amt > 1) {
            e.getItem().setAmount(amt - 1);
        } else if (amt == 1) {
            p.getInventory().remove(e.getItem());
        }
    }

    /**
     * Override this method, add an annotation, and call super.
     *
     * @param e
     */
    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent e) {
        for (ThrownEggTimer t : timers) {
            if (t.isFor(e.getEgg())) {
                e.setHatching(false);
                return;
            }
        }
    }

    /**
     * Override this method, add an annotation, and call super.
     *
     * @param e
     */
    @EventHandler
    public void onEggHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Egg)) {
            return;
        }

        Egg egg = (Egg) e.getEntity();

        ThrownEggTimer rem = null;

        for (ThrownEggTimer t : timers) {
            if (t.isFor(egg)) {
                t.run();
                rem = t;
                break;
            }
        }

        if (rem != null) {
            timers.remove(rem);
        }
    }

    private class ThrownEggTimer {

        private String player;

        private Egg egg;

        private long expire;

        public ThrownEggTimer(String player, Egg egg) {
            this.player = player;
            this.egg = egg;
            expire = System.currentTimeMillis() + ThrownEgg.this.detonateTime;
        }

        /**
         * Checks if this timer is for the given egg.
         *
         * @param e
         * @return
         */
        public boolean isFor(Egg e) {
            return e.equals(egg);
        }

        /**
         * Checks the time and runs it if it can.
         *
         * @return True if the timer is expired.
         */
        public boolean check() {
            if (egg.isDead()) {
                return true;
            }

            if (expire - System.currentTimeMillis() < 0) {
                run();
                return true;
            }

            return false;
        }

        /**
         * Runs this egg.
         */
        public void run() {
            Player p = Bukkit.getPlayerExact(player);
            if (p == null) {
                return;
            }

            if (ThrownEgg.this.canUseAt(p, egg.getLocation())
                    && ThrownEgg.this.detonate(p, egg.getLocation())) {
                return;
            }

            p.getInventory().addItem(ThrownEgg.this.create(1));
        }

    }
}
