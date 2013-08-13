package net.new_liberty.nltweaks.tweak;

import net.new_liberty.nltweaks.Tweak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Mob Tamer tweak.
 */
public class MobTamer extends Tweak {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if (b.getType() != Material.MOB_SPAWNER) {
            return;
        }

        ItemStack i = e.getItem();
        if (i.getType() != Material.MONSTER_EGG) {
            return;
        }

        EntityType t = EntityType.fromId(i.getDurability());
        if (t == null) {
            return;
        }

        CreatureSpawner s = (CreatureSpawner) b.getState();
        s.setSpawnedType(t);
        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have changed this spawner to spawn " + t.getName() + " mobs.");
        e.setCancelled(true);
        i.setAmount(i.getAmount() - 1);
    }
}
