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
    public void onEntityDamage(EntityDamageEvent e) {
        if (h.khorse.isHorse(e.getEntity())) {
            LivingEntity horse = (LivingEntity) e.getEntity();

            if (h.khorse.isOwnedHorse(horse.getUniqueId())) {
                if (e instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent e1 = (EntityDamageByEntityEvent) e;

                    if (e1.getDamager() instanceof Player) {
                        Player damager = (Player) e1.getDamager();

                        if (h.khorse.canMountHorse(damager, horse)) {
                            damager.sendMessage(ChatColor.GOLD + "You can't attack this horse, if you are the owner or member of it");
                            e1.setCancelled(true);
                        }
                    } else if (e1.getDamager() instanceof Projectile) {
                        Projectile projectile = (Projectile) e1.getDamager();

                        if (projectile.getShooter() instanceof Player) {
                            Player shooter = (Player) projectile.getShooter();

                            if (h.khorse.canMountHorse(shooter, horse)) {
                                shooter.sendMessage(ChatColor.GOLD + "You can't attack this horse, if you are the owner or member of it");
                                e.setCancelled(true);
                            }
                        }
                    }
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
            if (h.khorse.isHorse(entity) && h.khorse.isOwnedHorse(entity.getUniqueId())) {
                if (h.khorse.canMountHorse(p, entity)) {
                    p.sendMessage(ChatColor.GOLD + "You can't attack this horse if you are the owner or member of it");
                    e.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();

        if (h.khorse.isHorse(entity)) {
            if (h.khorse.isOwnedHorse(entity.getUniqueId())) {
                String ownerName = h.khorse.getHorseOwner(entity);
                Player owner = Bukkit.getPlayerExact(ownerName);

                if (owner.isOnline()) {
                    owner.sendMessage(ChatColor.RED + "One of your horses is dead (" + h.khorse.getHorseIdentifier(entity.getUniqueId()) + ")");
                }

                h.khorse.removeHorse(h.khorse.getHorseIdentifier(entity.getUniqueId()));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkUnloaded(ChunkUnloadEvent event) {
        Chunk c = event.getChunk();

        Entity[] entities = c.getEntities();

        for (Entity e : entities) {
            if (h.khorse.isHorse(e)) {
                if (h.khorse.isOwnedHorse(e.getUniqueId())) {
                    // We save horse location
                    Location loc = e.getLocation();
                    // TODO save horse location
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
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

        if (h.khorse.isHorse(tamedAnimal)) {
            player.sendMessage("You tamed a horse! Now right-click with a saddle on horse to protect it");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntered();

        if (h.khorse.isHorse(event.getVehicle())) {
            Horse horse = (Horse) event.getVehicle();

            if (h.khorse.isOwnedHorse(horse.getUniqueId())) {
                if (!h.khorse.canMountHorse(player, horse)) {
                    player.sendMessage("This horse belongs to " + ChatColor.AQUA + h.khorse.getHorseOwner(horse));

                    event.setCancelled(true);
                }
            }
        }
    }

}
