package net.new_liberty.horses;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import static net.new_liberty.nlcore.player.DonorRank.PREMIUM;

import net.new_liberty.nlcore.player.NLPlayer;
import net.new_liberty.nlcore.player.StaffRank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import org.bukkit.plugin.java.JavaPlugin;

public class HorseKeep extends JavaPlugin implements Listener {

    public String prefix = ChatColor.RED + "[" + ChatColor.GOLD + "HorseKeep" + ChatColor.RED + "] " + ChatColor.GREEN;

    public KHorse khorse;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        khorse = new KHorse(this, this.getConfig());

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("Enabled");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("horse")) {
            if (args.length == 0) {
                sender.sendMessage("=== " + ChatColor.GOLD + "[" + ChatColor.GREEN + "Commands List" + ChatColor.GOLD + "] " + ChatColor.RESET + "===");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse list|l");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse tp <identifier>");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse id|setid <identifier> <new-identifier>");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse tpall");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse unprotect|up <identifier>");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse reload");
            } else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command can only be executed by a player");
                    return true;
                }

                sender.sendMessage("=== " + ChatColor.GOLD + "[" + ChatColor.GREEN + "Owned Horses" + ChatColor.GOLD + "] " + ChatColor.RESET + "===");

                List<String> horsesList = this.khorse.getOwnedHorses((Player) sender);
                for (String horseId : horsesList) {
                    sender.sendMessage("- Identifier: " + ChatColor.AQUA + this.khorse.getHorseIdentifier(UUID.fromString(horseId)));
                }
            } else if (args[0].equalsIgnoreCase("setid") || args[0].equalsIgnoreCase("id")) {
                if (args.length < 2) {
                    sender.sendMessage(prefix + ChatColor.GOLD + "Missing horse identifier");
                    return true;
                }

                String horseIdentifier = args[1];

                if (!this.khorse.horseIdentifierExists(horseIdentifier)) {
                    sender.sendMessage(prefix + ChatColor.GOLD + "This horse doesn't exist");
                    return true;
                }

                if (!this.khorse.isHorseOwner(horseIdentifier, sender.getName())) {
                    sender.sendMessage(prefix + ChatColor.GOLD + "You don't own this horse");
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage(prefix + ChatColor.GOLD + "Missing horse new identifier");
                    return true;
                }

                Boolean hasWhiteSpace = false;
                for (char c : args[2].toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        hasWhiteSpace = true;
                    }
                }

                if (hasWhiteSpace) {
                    sender.sendMessage(prefix + ChatColor.GOLD + "Identifier may not contain whitespace");
                    return true;
                }

                if (this.khorse.isHorseIdentifierTaken(args[2])) {
                    sender.sendMessage(prefix + "Identifier has already been taken, please choose another.");
                    return true;
                }

                getConfig().set("horses." + this.khorse.getHorseUUID(args[1]) + ".identifier", args[2]);
                saveConfig();

                sender.sendMessage(prefix + "New identifier set to " + args[2]);
            } else if (args[0].equalsIgnoreCase("tp")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command can only be executed by a player");
                    return true;
                }

                Player player = (Player) sender;

                if (!player.hasPermission("horsekeep.tp")) {
                    player.sendMessage(prefix + ChatColor.RED + "You don't have permission to do this");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(prefix + ChatColor.GOLD + "Missing horse identifier");
                    return true;
                }

                String horseIdentifier = args[1];

                if (!this.khorse.horseIdentifierExists(horseIdentifier)) {
                    player.sendMessage(prefix + ChatColor.GOLD + "This horse doesn't exist");
                    return true;
                }

                if (!this.khorse.isHorseOwner(horseIdentifier, player.getName())) {
                    player.sendMessage(prefix + ChatColor.GOLD + "You don't own this horse");
                    return true;
                }

                UUID horseUUID = this.khorse.getHorseUUID(horseIdentifier);

                HorseTeleportResponse response = this.khorse.teleportHorse(horseUUID, player.getLocation());

                if (response.equals(HorseTeleportResponse.NOT_TELEPORTED_ENTITY_DELETED)) {
                    player.sendMessage(prefix + ChatColor.RED + "Horse " + horseIdentifier + " was not found (dead or deleted), removing from config...");
                    this.khorse.removeHorse(horseIdentifier);
                } else if (response.equals(HorseTeleportResponse.NOT_TELEPORTED_WRONG_WORLD)) {
                    player.sendMessage(prefix + ChatColor.GOLD + "Horse " + horseIdentifier + " was not teleported because located in another world");
                } else if (response.equals(HorseTeleportResponse.NOT_TELEPORTED)) {
                    player.sendMessage(prefix + ChatColor.GOLD + "Horse " + horseIdentifier + " was not teleported because of unknown error");
                } else if (response.equals(HorseTeleportResponse.NOT_TELEPORTED_STORED)) {
                    player.sendMessage(prefix + ChatColor.GOLD + "Horse " + horseIdentifier + " was not teleported because currently stored. Summon it by using /horse summon " + horseIdentifier);
                }

                player.sendMessage(prefix + "Done");
            } else if (args[0].equalsIgnoreCase("unprotect") || args[0].equalsIgnoreCase("up")) {
                if (args.length < 2) {
                    sender.sendMessage(prefix + ChatColor.GOLD + "Missing horse identifier");
                    return true;
                }

                String horseIdentifier = args[1];

                if (!this.khorse.horseIdentifierExists(horseIdentifier)) {
                    sender.sendMessage(prefix + ChatColor.GOLD + "This horse doesn't exist");
                    return true;
                }

                if (!this.khorse.isHorseOwner(horseIdentifier, sender.getName())) {
                    sender.sendMessage(prefix + ChatColor.GOLD + "You don't own this horse");
                    return true;
                }

                this.khorse.removeHorse(horseIdentifier);

                sender.sendMessage(prefix + "Horse is now un-protected");
            } else if (args[0].equalsIgnoreCase("getid")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command can only be executed by a player");
                    return true;
                }

                Player player = (Player) sender;

                if (!this.khorse.isOnHorse(player)) {
                    player.sendMessage(prefix + ChatColor.GOLD + "You must be riding a horse to use this command");
                    return true;
                }

                Horse horse = (Horse) player.getVehicle();

                if (!this.khorse.isOwnedHorse(horse)) {
                    player.sendMessage(prefix + ChatColor.GOLD + "This horse is not protected");
                    return true;
                }

                if (!this.khorse.isHorseOwner(player, horse)) {
                    player.sendMessage(prefix + ChatColor.GOLD + "You don't own this horse");
                    return true;
                }

                player.sendMessage(prefix + "Horse identifier: " + this.khorse.getHorseIdentifier(horse.getUniqueId()));
            } else if (args[0].equalsIgnoreCase("admin")) {
                if (!sender.hasPermission("horsekeep.admin")) {
                    sender.sendMessage(prefix + ChatColor.RED + "You don't have permission to do this");
                    return true;
                }

                if (args.length < 2) {
                    return true;
                }

                if (args[1].equalsIgnoreCase("list")) {
                    if (args.length < 3) {
                        sender.sendMessage(prefix + ChatColor.GOLD + "Missing player name");
                        return true;
                    }

                    String playerName = args[2];

                    sender.sendMessage("=== " + ChatColor.GOLD + "[" + ChatColor.GREEN + playerName + " Horses" + ChatColor.GOLD + "] " + ChatColor.RESET + "===");

                    List<String> horsesList = this.khorse.getOwnedHorses(playerName);

                    for (String horseId : horsesList) {
                        sender.sendMessage("- Identifier: " + ChatColor.AQUA + this.khorse.getHorseIdentifier(this.khorse.getHorseUUID(horseId)));
                    }
                }

            }
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent e) {

        if (this.khorse.isHorse(e.getEntity())) {
            LivingEntity horse = (LivingEntity) e.getEntity();

            if (this.khorse.isOwnedHorse(horse.getUniqueId())) {
                if (e instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent e1 = (EntityDamageByEntityEvent) e;

                    if (e1.getDamager() instanceof Player) {
                        Player damager = (Player) e1.getDamager();

                        if (getConfig().getBoolean("disableHorseDamage")) {
                            damager.sendMessage(prefix + ChatColor.GOLD + "You can't attack an owned horse");
                            e1.setCancelled(true);
                        } else if (this.khorse.canMountHorse(damager, horse) && getConfig().getBoolean("disableHorseDamageFromMembers")) {
                            damager.sendMessage(prefix + ChatColor.GOLD + "You can't attack this horse, if you are the owner or member of it");
                            e1.setCancelled(true);
                        }
                    } else if (e1.getDamager() instanceof Projectile) {
                        Projectile projectile = (Projectile) e1.getDamager();

                        if (projectile.getShooter() instanceof Player) {
                            Player shooter = (Player) projectile.getShooter();

                            if (getConfig().getBoolean("disableHorseDamage")) {
                                shooter.sendMessage(prefix + ChatColor.GOLD + "You can't attack an owned horse");
                                e.setCancelled(true);
                            } else if (this.khorse.canMountHorse(shooter, horse) && getConfig().getBoolean("disableHorseDamageFromMembers")) {
                                shooter.sendMessage(prefix + ChatColor.GOLD + "You can't attack this horse, if you are the owner or member of it");
                                e.setCancelled(true);
                            }
                        } else if (getConfig().getBoolean("disableHorseEnvironmentalDamage")) {
                            e.setCancelled(true);
                        }
                    } else if (getConfig().getBoolean("disableHorseEnvironmentalDamage")) {
                        e.setCancelled(true);
                    }
                } else if (getConfig().getBoolean("disableHorseEnvironmentalDamage")) {
                    e.setCancelled(true);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player thrower = (Player) e.getEntity().getShooter();
            Collection<LivingEntity> AffectedEntities = e.getAffectedEntities();
            Iterator<LivingEntity> it = AffectedEntities.iterator();

            boolean cancelEvent = false;

            String message = null;

            while (it.hasNext()) {
                LivingEntity entity = it.next();
                if (this.khorse.isHorse(entity)) {
                    if (this.khorse.isOwnedHorse(entity.getUniqueId())) {
                        if (getConfig().getBoolean("disableHorseDamage")) {
                            message = prefix + ChatColor.GOLD + "You can't attack an owned horse";
                            cancelEvent = true;
                        } else if (this.khorse.canMountHorse(thrower, entity) && getConfig().getBoolean("disableHorseDamageFromMembers")) {
                            message = prefix + ChatColor.GOLD + "You can't attack this horse, if you are the owner or member of it";
                            cancelEvent = true;
                        }
                    }
                }
            }

            if (cancelEvent) {
                thrower.sendMessage(message);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();

        if (this.khorse.isHorse(entity)) {
            if (this.khorse.isOwnedHorse(entity.getUniqueId())) {
                String ownerName = this.khorse.getHorseOwner(entity);
                Player owner = Bukkit.getPlayerExact(ownerName);

                if (owner.isOnline()) {
                    owner.sendMessage(prefix + ChatColor.RED + "One of your horses is dead (" + this.khorse.getHorseIdentifier(entity.getUniqueId()) + ")");
                }

                this.khorse.removeHorse(this.khorse.getHorseIdentifier(entity.getUniqueId()));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkUnloaded(ChunkUnloadEvent event) {
        Chunk c = event.getChunk();

        Entity[] entities = c.getEntities();

        for (Entity e : entities) {
            if (this.khorse.isHorse(e)) {
                if (this.khorse.isOwnedHorse(e.getUniqueId())) {
                    // We save horse location
                    Location loc = e.getLocation();
                    getConfig().set("horses." + e.getUniqueId() + ".lastpos", loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch());

                    this.saveConfig();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (this.khorse.isHorse(event.getRightClicked())) {

            Entity horse = event.getRightClicked();

            if (!this.khorse.isOwnedHorse(horse.getUniqueId())) {
                if (event.getPlayer().getItemInHand().getType() == Material.SADDLE) {
                    int limit = getHorseLimit(event.getPlayer());

                    if (this.khorse.getOwnedHorses(event.getPlayer()).size() >= limit) {
                        event.getPlayer().sendMessage(prefix + ChatColor.GOLD + "You have reached the maximum limit of horses you can protect - This horse will not be protected");
                        return;
                    }

                    if (event.getPlayer().getName().isEmpty()) {
                        event.getPlayer().sendMessage(prefix + ChatColor.RED + "Error while setting up protection");
                        return;
                    }

                    this.khorse.setHorseOwner(event.getPlayer(), horse);

                    horse.getWorld().playSound(horse.getLocation(), Sound.LEVEL_UP, 10.0F, 1.0F);

                    event.getPlayer().sendMessage(prefix + "You protected this horse! Now use " + ChatColor.GOLD + "/horse id " + this.khorse.getHorseIdentifier(this.khorse.getHorseUUID(horse)) + ChatColor.AQUA + " <new-name>");
                }
            } else {
                if (!this.khorse.canMountHorse(event.getPlayer(), horse)) {
                    event.getPlayer().sendMessage(prefix + ChatColor.RED + "This horse belongs to " + ChatColor.AQUA + this.khorse.getHorseOwner(horse));

                    if (event.getPlayer().hasPermission("horsekeep.admin")) {
                        event.getPlayer().sendMessage(prefix + "Horse Identifier: " + this.khorse.getHorseIdentifier(this.khorse.getHorseUUID(horse)));
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

        if (this.khorse.isHorse(tamedAnimal)) {
            player.sendMessage(prefix + "You tamed a horse! Now right-click with a saddle on horse to protect it");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntered();

        if (this.khorse.isHorse(event.getVehicle())) {
            Horse horse = (Horse) event.getVehicle();

            if (this.khorse.isOwnedHorse(horse.getUniqueId())) {
                if (!this.khorse.canMountHorse(player, horse)) {
                    player.sendMessage(prefix + "This horse belongs to " + ChatColor.AQUA + this.khorse.getHorseOwner(horse));

                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Gets the limit of horses a player can have,.
     *
     * @param l
     * @return
     */
    public int getHorseLimit(Player l) {
        NLPlayer p = new NLPlayer(l);
        if (p.getStaffRank() == StaffRank.ADMIN) {
            return 1000;
        }

        switch (p.getDonorRank()) {
            case PREMIUM:
                return 1;
            case HERO:
                return 2;
            case ELITE:
                return 3;
            case GUARDIAN:
                return 4;
            case CHAMPION:
                return 5;
        }

        return 0;
    }

}