package net.new_liberty.tweaks;

import net.new_liberty.nlcore.module.Module;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Makes spawners have different mobs assigned to them. These spawners are
 * mineable without silk touch (to promote raiding) and are also naturally
 * dropped by mobs.
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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.MOB_SPAWNER) {
            return;
        }

        CreatureSpawner s = (CreatureSpawner) e.getBlock().getState();
        EntityType t = s.getSpawnedType();

        ItemStack i = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(WordUtils.capitalize(t.name()).replace('_', ' '));
        i.setItemMeta(m);

        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), i);
    }

}
