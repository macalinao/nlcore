package net.new_liberty.horses;

import net.new_liberty.horses.commands.DisownHorseCommand;
import net.new_liberty.horses.commands.HorsesCommand;
import net.new_liberty.horses.commands.TPHorseCommand;
import net.new_liberty.nlcore.database.DB;
import net.new_liberty.nlcore.module.Module;

import net.new_liberty.nlcore.player.NLPlayer;
import net.new_liberty.nlcore.player.StaffRank;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Horses extends Module implements Listener {

    private HorsesListener hl;

    private HorseManager horses;

    @Override
    public void onEnable() {
        DB.i().update("CREATE TABLE IF NOT EXISTS horses ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "uuid VARCHAR(40) NOT NULL,"
                + "owner VARCHAR(16) NOT NULL,"
                + "name VARCHAR(16),"
                + "last_world VARCHAR(16),"
                + "last_x INT(10),"
                + "last_y INT(10),"
                + "last_z INT(10),"
                + "PRIMARY KEY (id))");

        hl = new HorsesListener(this);
        addListener(hl);

        addPermission("nlhorses.admin", "Admin permission for NL horses.");

        addCommand("disownhorse", new DisownHorseCommand(this));
        addCommand("horses", new HorsesCommand(this));
        addCommand("tphorse", new TPHorseCommand(this));

        horses = new HorseManager();
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