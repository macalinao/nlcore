package net.new_liberty.nltweaks.tweak;

import net.new_liberty.nltweaks.Tweak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Gets rid of TNT minecarts on the server.
 */
public class NoTNTMinecart extends Tweak {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            return;
        }

        if (e.getItem().getType() == Material.EXPLOSIVE_MINECART) {
            e.getPlayer().sendMessage(ChatColor.RED + "TNT Minecarts are currently disabled due to a bug in Minecraft.");
            e.setCancelled(true);
        }
    }
}
