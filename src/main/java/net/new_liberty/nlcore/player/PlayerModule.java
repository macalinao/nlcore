/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nlcore.player;

import net.new_liberty.nlcore.player.StaffRank;
import net.new_liberty.nlcore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

/**
 * Handles player ranks based on permissions. (Donation and staff ranks)
 */
public class PlayerModule extends Module {

    @Override
    public void onEnable() {
        // Set up permissions
        for (StaffRank r : StaffRank.values()) {
            Bukkit.getPluginManager().addPermission(new Permission(r.getPermission()));
        }

        for (DonorRank r : DonorRank.values()) {
            Bukkit.getPluginManager().addPermission(new Permission(r.getPermission()));
        }
    }

}
