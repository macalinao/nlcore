package net.new_liberty.nltweaks.tweak;

import net.new_liberty.nltweaks.NLTweaks;
import net.new_liberty.nltweaks.Tweak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Prevents placing creeper eggs so close to chests.
 */
public class CreeperChest extends Tweak {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getItem() == null || e.getItem().getType() != Material.MONSTER_EGG) {
            return;
        }

        for (BlockFace face : BlockFace.values()) {
            if (e.getClickedBlock().getRelative(face).getType() == Material.CHEST) {
                e.getPlayer().sendMessage(ChatColor.RED + "You cannot place a mob egg that close to a chest!");
                e.setCancelled(true);
                return;
            }
        }
    }
}
