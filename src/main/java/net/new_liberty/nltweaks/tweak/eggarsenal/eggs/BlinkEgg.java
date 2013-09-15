package net.new_liberty.nltweaks.tweak.eggarsenal.eggs;

import java.util.ArrayList;
import java.util.List;
import net.new_liberty.nltweaks.NLTweaks;
import net.new_liberty.nltweaks.tweak.eggarsenal.SpecialEgg;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BlinkEgg extends SpecialEgg {

    private static final int BLINK_EXPIRE_MS = 3000;

    private List<BlinkTimer> timers = new ArrayList<BlinkTimer>();

    public BlinkEgg() {
        super("Blink Egg");
        description = "Teleports you a short distance.";
        eggType = EntityType.ENDERMAN;
        allowInCombat = false;
        cooldown = 10;

        (new BukkitRunnable() {
            @Override
            public void run() {
                List<BlinkTimer> rem = new ArrayList<BlinkTimer>();
                for (BlinkTimer t : timers) {
                    if (t.check()) {
                        rem.add(t);
                    }
                }

                for (BlinkTimer r : rem) {
                    timers.remove(r);
                }
            }

        }).runTaskTimer(NLTweaks.getInstance(), 2, 2);
    }

    @EventHandler
    public void onEggUse(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                || !(e.hasItem())) {
            return;
        }

        if (!isInstance(e.getItem())) {
            return;
        }

        if (!checkCanUse(e.getPlayer())) {
            return;
        }

        Player p = e.getPlayer();
        timers.add(new BlinkTimer(p.getName(), p.launchProjectile(Egg.class)));
        e.setCancelled(true);
    }

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent e) {
        for (BlinkTimer t : timers) {
            if (t.isFor(e.getEgg())) {
                e.setHatching(false);
                return;
            }
        }
    }

    @EventHandler
    public void onEggHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Egg)) {
            return;
        }

        Egg egg = (Egg) e.getEntity();

        BlinkTimer rem = null;

        for (BlinkTimer t : timers) {
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

    private class BlinkTimer {

        private String player;

        private Egg egg;

        private long expire;

        public BlinkTimer(String player, Egg egg) {
            this.player = player;
            this.egg = egg;
            expire = System.currentTimeMillis() + BLINK_EXPIRE_MS;
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
         * Runs this teleport.
         */
        public void run() {
            Player p = Bukkit.getPlayerExact(player);
            if (p == null) {
                return;
            }

            p.teleport(egg);
            p.getWorld().playSound(p.getLocation(), Sound.WITHER_SHOOT, 1.0f, 1.0f);
        }

    }
}
