package net.new_liberty.core.tweaks;

import net.new_liberty.core.module.Module;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Mob Tamer tweak.
 */
public class MobTamer extends Module {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block b = e.getClickedBlock();
        if (b.getType() != Material.MOB_SPAWNER) {
            return;
        }

        ItemStack i = e.getItem();
        if (i == null) {
            return;
        }

        if (i.getType() != Material.MONSTER_EGG) {
            return;
        }

        EntityType t = EntityType.fromId(i.getDurability());
        if (t == null) {
            return;
        }

        if (t == EntityType.OCELOT || t == EntityType.GHAST || t == EntityType.CREEPER) {
            e.getPlayer().sendMessage(ChatColor.YELLOW + "You can't spawn " + t.getName() + " mobs. Check the shop for creeper spawners.");
            return;
        }

        CreatureSpawner s = (CreatureSpawner) b.getState();
        s.setSpawnedType(t);
        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have changed this spawner to spawn " + t.getName() + " mobs.");
        e.setCancelled(true);
        int amt = i.getAmount() - 1;
        if (amt > 0) {
            i.setAmount(amt);
        } else {
            e.getPlayer().setItemInHand(null);
        }
    }
}
