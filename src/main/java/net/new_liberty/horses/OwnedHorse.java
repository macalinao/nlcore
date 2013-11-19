/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.horses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import net.new_liberty.nlcore.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 *
 * @author simplyianm
 */
public class OwnedHorse {

    private Entity e;

    private int id;

    private UUID uuid;

    private String owner;

    private String name;

    private Location lastLocation;

    public OwnedHorse(ResultSet rs) {
        setData(rs);
    }

    public OwnedHorse(Entity e, ResultSet rs) {
        this.e = e;
        setData(rs);
    }

    public Entity getEntity() {
        if (e != null) {
            return e;
        }

        Chunk last = lastLocation.getChunk();
        if (!last.isLoaded()) {
            last.load();
        }

        for (Entity ent : last.getEntities()) {
            if (e.getUniqueId().toString().equalsIgnoreCase(uuid.toString())) {
                e = ent;
                return e;
            }
        }

        return null;
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
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
            uuid = UUID.fromString(rs.getString("uuid"));
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

    public void saveLastLocation() {
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
