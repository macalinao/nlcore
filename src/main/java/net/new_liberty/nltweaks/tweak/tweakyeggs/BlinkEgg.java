package net.new_liberty.nltweaks.tweak.tweakyeggs;

import net.new_liberty.nltweaks.TweakUtil;
import net.new_liberty.nltweaks.tweak.TweakyEgg;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlinkEgg extends TweakyEgg {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEggUse(PlayerInteractEvent event) {
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				|| !(event.hasItem())) {
			return;
		}

		final ItemStack held = event.getItem();
		if (held.getItemMeta().getDisplayName().equals("Blink Egg")) {
			final Player player = event.getPlayer();
			final Egg egg = player.launchProjectile(Egg.class);
			final Block block = player.getTargetBlock(null, EGG_RANGE);
			final Location loc = TweakUtil.getSafeDestination(block
					.getLocation());

			player.teleport(loc);
			event.setCancelled(true);
		}
	}
}
