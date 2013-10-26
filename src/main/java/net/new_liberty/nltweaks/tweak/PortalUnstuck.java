/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nltweaks.tweak;

import net.new_liberty.nltweaks.Tweak;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * PortalUnstuck.jar
 */
public class PortalUnstuck extends Tweak {

    @EventHandler
    public void onLogin(final PlayerJoinEvent e) {
        if (e.getPlayer().getLocation().add(0.0D, 1.0D, 0.0D).getBlock().getType().equals(Material.PORTAL)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(e.getPlayer(), "spawn");
                    e.getPlayer().sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "You are being teleported to spawn. Please stand still!");
                }

            }, 20L);
        }
    }

}
