package net.new_liberty.nltweaks.tweak;

import net.new_liberty.nltweaks.NLTweaks;
import net.new_liberty.nltweaks.Tweak;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Nerfs Strength Pots back to how they were in 1.5.
 */
public class NerfStrengthPots extends Tweak {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getDamager();
        int level = -1;

        for (PotionEffect f : p.getActivePotionEffects()) {
            if (f.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                level = f.getAmplifier();
                break;
            }
        }

        if (level == -1) {
            return;
        }

        double newDmg = e.getDamage();
        if (level == 0) { // Strength I -- +130%
            newDmg /= 2.3;
            newDmg += 3;

        } else if (level == 1) {  // Strength II -- +260%
            newDmg /= 3.6;
            newDmg += 6;
        }

        e.setDamage(newDmg);
    }
}
