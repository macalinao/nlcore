package net.new_liberty.horses;

import java.util.List;
import java.util.UUID;
import net.new_liberty.nlcore.database.Database;
import static net.new_liberty.nlcore.player.DonorRank.PREMIUM;

import net.new_liberty.nlcore.player.NLPlayer;
import net.new_liberty.nlcore.player.StaffRank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Horses extends JavaPlugin implements Listener {

    public String prefix = ChatColor.RED + "[" + ChatColor.GOLD + "HorseKeep" + ChatColor.RED + "] " + ChatColor.GREEN;

    public KHorse khorse;

    private HorsesListener hl;

    private HorseManager horses;

    @Override
    public void onEnable() {
        Database.i().update("CREATE TABLE IF NOT EXISTS horses ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "uuid VARCHAR(40) NOT NULL,"
                + "owner VARCHAR(16) NOT NULL,"
                + "name VARCHAR(16),"
                + "last_world VARCHAR(16),"
                + "last_x INT(10),"
                + "last_y INT(10),"
                + "last_z INT(10),"
                + "PRIMARY KEY (id))");

        khorse = new KHorse(this, this.getConfig());
        hl = new HorsesListener(this);
        getServer().getPluginManager().registerEvents(hl, this);

        horses = new HorseManager();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("horse")) {
            if (args.length == 0) {
                sender.sendMessage("=== " + ChatColor.GOLD + "[" + ChatColor.GREEN + "Commands List" + ChatColor.GOLD + "] " + ChatColor.RESET + "===");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse list|l");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse tp <identifier>");
                sender.sendMessage("- " + ChatColor.AQUA + "/horse unprotect|up <identifier>");
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
            }
            return true;
        }
        return false;
    }

    public HorseManager getHorses() {
        return horses;
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