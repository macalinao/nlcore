package net.new_liberty.tweaks;

import java.util.List;
import java.util.Random;
import net.new_liberty.nlcore.NLCore;
import net.new_liberty.nlcore.module.Module;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 * Makes spawners have different mobs assigned to them. These spawners are
 * mineable without silk touch (to promote raiding) and are also naturally
 * dropped by mobs.
 */
public class EasySpawners extends Module {

    public static final double DROP_CHANCE = 0.01;

    private Random r = new Random();

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

        dropSpawner(t, e.getBlock().getLocation());
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == SpawnReason.NATURAL) {
            e.getEntity().setMetadata("n", new FixedMetadataValue(NLCore.i(), true));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        switch (e.getEntityType()) {
            case CREEPER:
            case SKELETON:
            case SPIDER:
            case ZOMBIE:
            case SLIME:
            case CAVE_SPIDER:
            case BAT:
            case PIG:
            case SHEEP:
            case COW:
            case CHICKEN:
            case OCELOT:
                break;
            default: // Anything not allowed
                return;
        }

        List<MetadataValue> vl = e.getEntity().getMetadata("n");
        for (MetadataValue m : vl) {
            if (m.getOwningPlugin().equals(NLCore.i()) && m.asBoolean()) {
                // yes, this is natural
                if (r.nextDouble() < DROP_CHANCE) {
                    dropSpawner(e.getEntityType(), e.getEntity().getLocation());
                }
                return;
            }
        }
    }

    private void dropSpawner(EntityType e, Location l) {
        ItemStack i = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(WordUtils.capitalize(e.name()).replace('_', ' '));
        i.setItemMeta(m);
        l.getWorld().dropItemNaturally(l, i);
    }

}
