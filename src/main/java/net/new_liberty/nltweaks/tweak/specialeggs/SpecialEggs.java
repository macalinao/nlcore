package net.new_liberty.nltweaks.tweak.specialeggs;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;
import java.util.HashMap;
import java.util.Map;
import net.new_liberty.nltweaks.Tweak;
import net.new_liberty.nltweaks.tweak.specialeggs.eggs.BlinkEgg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpecialEggs extends Tweak {

    private CombatTagApi combatTag;

    private Map<String, EggCooldowns> cds = new HashMap<String, EggCooldowns>();

    private Map<String, SpecialEgg> eggs = new HashMap<String, SpecialEgg>();

    private WorldGuardPlugin wg;

    @Override
    public void onEnable() {
        combatTag = new CombatTagApi((CombatTag) Bukkit.getPluginManager().getPlugin("CombatTag"));
        wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

        addEgg(new BlinkEgg());

        for (SpecialEgg egg : eggs.values()) {
            egg.initialize(this);
        }
    }

    /**
     * Gets the cooldown timers of a player.
     *
     * @param player
     * @return
     */
    public EggCooldowns getCooldowns(String player) {
        EggCooldowns cd = cds.get(player);
        if (cd == null) {
            cd = new EggCooldowns(player);
            cds.put(player, cd);
        }
        return cd;
    }

    public boolean isInCombat(Player player) {
        return combatTag.isInCombat(player);
    }

    public WorldGuardPlugin getWg() {
        return wg;
    }

    private void addEgg(SpecialEgg egg) {
        eggs.put(egg.getName(), egg);
        Bukkit.getPluginManager().registerEvents(egg, plugin);
    }

}
