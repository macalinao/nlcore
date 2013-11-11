package net.new_liberty.nltweaks.tweak;

import net.new_liberty.nltweaks.Tweak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Makes spawners easy.
 */
public class EasySpawners extends Tweak {

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
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        if (e.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }

        if (e.getSlotType() != SlotType.RESULT || e.getSlot() != 2) {
            return;
        }

        ItemStack i = e.getCurrentItem();
        if (i == null || i.getType() != Material.MOB_SPAWNER) {
            return;
        }

        Player p = (Player) e.getWhoClicked();
        p.sendMessage(ChatColor.RED + "You can't rename mob spawners.");
        e.setCancelled(true);
        p.closeInventory();
    }

}
