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
        if (h.khorse.isHorse(event.getRightClicked())) {

            Entity horse = event.getRightClicked();

            if (!h.khorse.isOwnedHorse(horse.getUniqueId())) {
                if (event.getPlayer().getItemInHand().getType() == Material.SADDLE) {
                    int limit = h.getHorseLimit(event.getPlayer());

                    if (h.khorse.getOwnedHorses(event.getPlayer()).size() >= limit) {
                        event.getPlayer().sendMessage(ChatColor.GOLD + "You have reached the maximum limit of horses you can protect - This horse will not be protected");
                        return;
                    }

                    if (event.getPlayer().getName().isEmpty()) {
                        event.getPlayer().sendMessage(ChatColor.RED + "Error while setting up protection");
                        return;
                    }

                    h.khorse.setHorseOwner(event.getPlayer(), horse);

                    horse.getWorld().playSound(horse.getLocation(), Sound.LEVEL_UP, 10.0F, 1.0F);

                    event.getPlayer().sendMessage("You protected this horse! Now use " + ChatColor.GOLD + "/horse id " + h.khorse.getHorseIdentifier(h.khorse.getHorseUUID(horse)) + ChatColor.AQUA + " <new-name>");
                }
            } else {
                if (!h.khorse.canMountHorse(event.getPlayer(), horse)) {
                    event.getPlayer().sendMessage(ChatColor.RED + "This horse belongs to " + ChatColor.AQUA + h.khorse.getHorseOwner(horse));

                    if (event.getPlayer().hasPermission("horsekeep.admin")) {
                        event.getPlayer().sendMessage("Horse Identifier: " + h.khorse.getHorseIdentifier(h.khorse.getHorseUUID(horse)));
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAnimalTame(EntityTameEvent e) {
        LivingEntity tamedAnimal = e.getEntity();
        Player player = (Player) e.getOwner();

        if (tamedAnimal instanceof Horse) {
            player.sendMessage("You tamed a horse! Now right-click with a saddle on horse to protect it");
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
