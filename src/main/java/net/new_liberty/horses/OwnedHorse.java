/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.horses;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.new_liberty.nlcore.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 *
 * @author simplyianm
 */
public class OwnedHorse {

    private final Entity e;

    private int id;

    private String owner;

    private String name;

    private Location lastLocation;

    public OwnedHorse(Entity e, ResultSet rs) {
        this.e = e;
        setData(rs);
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    private void setData(ResultSet rs) {
        try {
            id = rs.getInt("id");
            owner = rs.getString("owner");
            name = rs.getString("name");

            String lastWorldS = rs.getString("last_world");
            World lastWorld = Bukkit.getWorld(lastWorldS);
            if (lastWorld == null) {
                return;
            }

            int lastX = rs.getInt("last_x");
            int lastY = rs.getInt("last_y");
            int lastZ = rs.getInt("last_z");

            lastLocation = new Location(lastWorld, lastX, lastY, lastZ);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateLastLocation() {
        Location l = e.getLocation();
        Database.i().update("UPDATE horses SET last_world = ?, last_x = ?, last_y = ?, last_z = ? WHERE id = ?",
                l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    /**
     * Deletes this horse. Note: the reference will not be gone, so keep this in
     * mind after using this method.
     */
    public void delete() {
        Database.i().update("DELETE FROM horses WHERE id = ?", id);
    }

}
