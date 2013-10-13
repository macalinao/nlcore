/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nltweaks.tweak.specialeggs.eggs;

import com.sk89q.worldguard.bukkit.SpongeUtil;
import java.util.LinkedList;
import java.util.Queue;
import net.new_liberty.nltweaks.NLTweaks;
import net.new_liberty.nltweaks.tweak.specialeggs.SpecialEgg;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The yung sponge egg.
 *
 * @author simplyianm
 */
public class SpongeEgg extends SpecialEgg {

    public static final long EXPIRY_TIME = 60 * 1000;

    private Queue<SpongeBlock> sponges = new LinkedList<SpongeBlock>();

    public SpongeEgg() {
        super("Sponge Egg");
        description = "Soaks up water where this egg is placed.";
        eggType = EntityType.OCELOT;
        cooldown = 5;
        usePlayerForLocationCheck = false;
    }

    @Override
    public void onEnable() {
        (new BukkitRunnable() {
            @Override
            public void run() {
                for (;;) {
                    SpongeBlock sponge = sponges.peek();
                    if (sponge != null && sponge.isExpired()) {
                        sponge.destroyWithSmoke();
                        sponges.remove();
                    } else {
                        break;
                    }
                }
            }

        }).runTaskTimer(NLTweaks.getInstance(), 20, 20); // 20 ticks per second
    }

    @Override
    public void onDisable() {
        for (SpongeBlock sponge : sponges) {
            sponge.destroy();
        }
    }

    @EventHandler
    public void onEggUse(PlayerInteractEvent e) {
        // Check for block click
        if (e.getClickedBlock() == null || e.getAction() != Action.RIGHT_CLICK_BLOCK || !e.hasItem()) {
            return;
        }

        if (!isInstance(e.getItem())) {
            return;
        }
        e.setCancelled(true);

        if (!checkCanUse(e.getPlayer())) {
            return;
        }

        // Get block to place the sponge
        Block block = e.getClickedBlock().getRelative(e.getBlockFace());
        if (!(block.getType().equals(Material.AIR)
                || block.getType().equals(Material.STATIONARY_WATER)
                || block.getType().equals(Material.STATIONARY_LAVA)
                || block.getType().equals(Material.WATER)
                || block.getType().equals(Material.LAVA))) {
            return;
        }

        if (!canUseAt(e.getPlayer(), block.getLocation())) {
            return;
        }

        // Consume one ocelot egg
        int amt = e.getItem().getAmount();
        if (amt > 1) {
            e.getItem().setAmount(amt - 1);
        } else if (amt == 1) {
            e.getPlayer().getInventory().remove(e.getItem());
        }

        // Set block as sponge
        block.setType(Material.SPONGE);

        // Remove water
        SpongeUtil.clearSpongeWater(ea.getWg(), block.getWorld(), block.getX(), block.getY(), block.getZ());

        // Schedule a sponge for extermination
        SpongeBlock sponge = new SpongeBlock(block.getLocation(), System.currentTimeMillis() + EXPIRY_TIME);
        sponges.add(sponge);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.SPONGE) {
            return;
        }

        for (SpongeBlock s : sponges) {
            if (s.isBlock(e.getBlock())) {
                s.destroy();
            }
        }
    }

    /**
     * Represents one of our sponges.
     */
    private class SpongeBlock {

        private Location loc;

        private long expire;

        public SpongeBlock(Location loc, long expire) {
            this.loc = loc;
            this.expire = expire;
        }

        /**
         * Checks if a block is this block
         *
         * @param b
         * @return
         */
        public boolean isBlock(Block b) {
            return b.getLocation().equals(loc);
        }

        /**
         * Checks if the sponge is expired.
         *
         * @return
         */
        public boolean isExpired() {
            return expire < System.currentTimeMillis();
        }

        /**
         * Destroys the sponge.
         */
        public void destroy() {
            loc.getBlock().setType(Material.AIR);
            SpongeUtil.addSpongeWater(ea.getWg(), loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }

        /**
         * Destroys the sponge with smoke.
         */
        public void destroyWithSmoke() {
            destroy();
            loc.getWorld().playEffect(loc, Effect.SMOKE, 0);
        }

        @Override
        public String toString() {
            return "Sponge at " + loc.toString() + " that expires at " + expire;
        }

    }
}
