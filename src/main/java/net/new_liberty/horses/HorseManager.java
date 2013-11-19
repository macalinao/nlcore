/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.horses;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.new_liberty.nlcore.database.Database;
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
        Database.i().update("INSERT INTO horses "
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

        return Database.i().query("SELECT * FROM horses WHERE uuid = ?", new ResultSetHandler<OwnedHorse>() {
            @Override
            public OwnedHorse handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }

                return new OwnedHorse(e, rs);
            }

        }, e.getUniqueId());
    }

}
