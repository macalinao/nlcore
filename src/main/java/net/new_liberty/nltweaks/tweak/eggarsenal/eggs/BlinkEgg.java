package net.new_liberty.nltweaks.tweak.eggarsenal.eggs;

import net.new_liberty.nltweaks.TweakUtil;
import net.new_liberty.nltweaks.tweak.eggarsenal.SpecialEgg;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlinkEgg extends SpecialEgg {

    public BlinkEgg() {
        super("Blink Egg");
        description = "Teleports you a short distance.";
        eggType = EntityType.ENDERMAN;
    }

    @EventHandler
    public void onEggUse(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                || !(event.hasItem())) {
            return;
        }

        ItemStack held = event.getItem();
        if (!isInstance(held)) {
            return;
        }

        Player player = event.getPlayer();
        Egg egg = player.launchProjectile(Egg.class);
        Block block = player.getTargetBlock(null, 20); // TODO fix code
        Location loc = TweakUtil.getSafeDestination(block.getLocation());

        player.teleport(loc);
        event.setCancelled(true);
    }

}
