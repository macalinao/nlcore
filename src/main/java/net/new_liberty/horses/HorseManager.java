/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.horses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.new_liberty.nlcore.database.DB;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;

/**
 * Manages our horse stuff.
 */
public class HorseManager {

    /**
     * Creates a horse ownership. Does not check if the protection already
     * exists.
     *
     * @param e
     * @param owner
     */
    public void createHorse(Entity e, String owner) {
        Location l = e.getLocation();
        DB.i().update("INSERT INTO horses "
                + "(uuid, owner, last_world, last_x, last_y, last_z) "
                + "VALUES (?, ?, ?, ?, ?, ?)", e.getUniqueId().toString(), owner,
                l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    /**
     * Gets a horse ownership.
     *
     * @param e
     * @return
     */
    public OwnedHorse getHorse(final Entity e) {
        if (!(e instanceof Horse)) {
            return null;
        }

        return DB.i().query("SELECT * FROM horses WHERE uuid = ?", new ResultSetHandler<OwnedHorse>() {
            @Override
            public OwnedHorse handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }

                return new OwnedHorse(e, rs);
            }

        }, e.getUniqueId());
    }

    public OwnedHorse getHorse(String owner, String callName) {
        OwnedHorse o = DB.i().query("SELECT * FROM horses WHERE owner = ? AND LOWER(name) LIKE ?", new ResultSetHandler<OwnedHorse>() {
            @Override
            public OwnedHorse handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }

                return new OwnedHorse(rs);
            }

        }, owner, callName.toLowerCase() + "%");

        if (o != null) {
            return o;
        }

        // A horse with no name
        int id;
        try {
            id = Integer.parseInt(callName);
        } catch (NumberFormatException ex) {
            return null;
        }

        return DB.i().query("SELECT * FROM horses WHERE id = ? AND owner = ?", new ResultSetHandler<OwnedHorse>() {
            @Override
            public OwnedHorse handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }

                return new OwnedHorse(rs);
            }

        }, id, owner);
    }

    /**
     * Gets all the horses owned by a player.
     *
     * @param owner
     * @return
     */
    public List<OwnedHorse> getHorses(String owner) {
        return DB.i().query("SELECT * FROM horses WHERE owner = ?", new ResultSetHandler<List<OwnedHorse>>() {
            @Override
            public List<OwnedHorse> handle(ResultSet rs) throws SQLException {
                List<OwnedHorse> horses = new ArrayList<OwnedHorse>();
                while (rs.next()) {
                    horses.add(new OwnedHorse(rs));
                }
                return horses;
            }

        }, owner);
    }

}
