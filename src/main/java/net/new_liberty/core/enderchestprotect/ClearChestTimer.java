package net.new_liberty.core.enderchestprotect;

import org.bukkit.Material;

/**
 * Timer used for the /ecclear command.
 */
public class ClearChestTimer {
    private final EnderChestProtect plugin;

    private final String player;

    private final long expire;

    public ClearChestTimer(EnderChestProtect plugin, String player) {
        this.plugin = plugin;
        this.player = player;
        this.expire = System.currentTimeMillis() + 30000L;
    }

    public String getPlayer() {
        return player;
    }

    public long getExpire() {
        return expire;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expire;
    }

    public void clearChests() {
        for (EnderChest ec : plugin.getECManager().getChests(player)) {
            ec.destroy();
            ec.getLocation().getBlock().setType(Material.AIR);
        }
    }
}
