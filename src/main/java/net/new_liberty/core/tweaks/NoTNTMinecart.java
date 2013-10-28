package net.new_liberty.core.tweaks;

import net.new_liberty.core.NLCore;
import net.new_liberty.core.module.Module;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Gets rid of TNT minecarts on the server.
 */
public class NoTNTMinecart extends Module {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() != null && e.getItem().getType() == Material.EXPLOSIVE_MINECART) {
            e.getPlayer().sendMessage(ChatColor.RED + "TNT Minecarts are currently disabled due to a bug in Minecraft.");
            e.setCancelled(true);
        }
    }
}
