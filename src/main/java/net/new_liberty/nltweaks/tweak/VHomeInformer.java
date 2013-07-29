package net.new_liberty.nltweaks.tweak;

import net.new_liberty.nltweaks.NLTweaks;
import net.new_liberty.nltweaks.Tweak;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Informs players of /vhome and /vsethome if they don't have access to homes.
 */
public class VHomeInformer extends Tweak {
    public VHomeInformer(NLTweaks plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission("essentials.sethome")) { // Check if they have access to set homes
            return;
        }

        String c = e.getMessage();
        if (c.equals("/home") || c.equals("/sethome") || c.startsWith("/home") || c.startsWith("/sethome")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You don't have access to this command. "
                    + "Try " + ChatColor.YELLOW + "/vsethome" + ChatColor.RED
                    + " and " + ChatColor.YELLOW + "/vhome" + ChatColor.RED + " to set homes.");
        }
    }
}
