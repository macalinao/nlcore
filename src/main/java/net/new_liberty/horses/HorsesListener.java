/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.horses;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Listener for Horses.
 */
public class HorsesListener implements Listener {

    private final Horses h;

    public HorsesListener(Horses h) {
        this.h = h;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        OwnedHorse o = h.getHorses().getHorse(e.getEntity());
        if (o == null) {
            return;
        }

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();

            if (o.getOwner().equals(damager.getName())) {
                damager.sendMessage(ChatColor.GOLD + "You can't attack this horse, if you are the owner or member of it");
                e.setCancelled(true);
            }
        } else if (e.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) e.getDamager();

            if (projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();

                if (o.getOwner().equals(shooter.getName())) {
                    shooter.sendMessage(ChatColor.GOLD + "You can't attack this horse, if you are the owner or member of it");
                    e.setCancelled(true);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent e) {
        Entity ent = e.getEntity().getShooter();
        if (!(ent instanceof Player)) {
            return;
        }
        Player p = (Player) ent;

        for (LivingEntity entity : e.getAffectedEntities()) {
            OwnedHorse o = h.getHorses().getHorse(entity);
            if (o == null) {
                continue;
            }

            if (o.getOwner().equals(p.getName())) {
                p.sendMessage(ChatColor.RED + "You can't attack this horse if you are the owner of it.");
                e.setCancelled(true);
                return;
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();

        OwnedHorse o = h.getHorses().getHorse(entity);
        if (o == null) {
            return;
        }

        Player owner = Bukkit.getPlayerExact(o.getOwner());
        if (owner != null && owner.isOnline()) {
            String name = o.getName();
            if (name != null) {
                owner.sendMessage(ChatColor.YELLOW + "Your horse '" + o.getName() + "' has died.");
            } else {
                owner.sendMessage(ChatColor.YELLOW + "One of your horses has died.");
            }
        }

        o.delete();
    }

    @EventHandler
    public void onChunkUnloaded(ChunkUnloadEvent event) {
        Chunk c = event.getChunk();

        Entity[] entities = c.getEntities();

        for (Entity e : entities) {
            OwnedHorse o = h.getHorses().getHorse(e);
            if (o != null) {
                o.saveLastLocation();
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Entity e = event.getRightClicked();
        OwnedHorse o = h.getHorses().getHorse(event.getRightClicked());
        if (!(e instanceof Horse)) {
            return;
        }

        Player p = event.getPlayer();

        if (o != null) {
            // Check if owner of horse
            if (!o.getOwner().equals(p.getName())) {
                p.sendMessage(ChatColor.RED + "You can't mount this horse as it belongs to " + ChatColor.YELLOW + o.getOwner() + ChatColor.RED + ".");

                if (p.hasPermission("horsekeep.admin")) {
                    p.sendMessage("Horse Identifier: " + o.getId());
                }

                event.setCancelled(true);
            }
            return;
        }

        if (p.getItemInHand().getType() != Material.SADDLE) {
            return;
        }

        int limit = h.getHorseLimit(p);
        int amt = h.getHorses().getHorses(p.getName()).size();

        if (amt >= limit) {
            p.sendMessage(ChatColor.RED + "You have reached the maximum limit of horses you can protect (" + amt + "). This horse will not be protected.");
            return;
        }

        h.getHorses().createHorse(e, p.getName());
        e.getWorld().playSound(e.getLocation(), Sound.LEVEL_UP, 10.0F, 1.0F);
        p.sendMessage(ChatColor.YELLOW + "You protected this horse! You should name it with a nametag.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAnimalTame(EntityTameEvent e) {
        LivingEntity tamedAnimal = e.getEntity();
        Player player = (Player) e.getOwner();

        if (tamedAnimal instanceof Horse) {
            player.sendMessage("You tamed a horse! Now right-click with a saddle on horse to protect it.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntered();

        OwnedHorse o = h.getHorses().getHorse(event.getVehicle());
        if (o == null) {
            return;
        }

        if (!o.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED
                    + "You cannot ride this horse as it belongs to " + ChatColor.YELLOW + o.getOwner() + ChatColor.RED + ".");
            event.setCancelled(true);
        }
    }

}
