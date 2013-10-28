package net.new_liberty.core.tweaks;

import net.new_liberty.core.module.Module;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Makes spawners easy.
 */
public class EasySpawners extends Module {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        if (b.getType() != Material.MOB_SPAWNER) {
            return;
        }

        String type = "Pig";

        ItemMeta m = event.getItemInHand().getItemMeta();
        if (!m.hasDisplayName()) {
            return;
        }
        type = m.getDisplayName();
        type = type.substring(0, type.length() - " Spawner".length());
        type = type.toUpperCase().replace(' ', '_');

        EntityType t = EntityType.valueOf(type);

        CreatureSpawner s = (CreatureSpawner) b.getState();
        s.setSpawnedType(t);
    }
}
