/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.specialeggs;

import java.util.HashMap;
import java.util.Map;

/**
 * Cooldowns for a player.
 */
public class EggCooldowns {

    /**
     * Player
     */
    private final String player;

    /**
     * Stores the end time for the cooldowns.
     */
    private Map<String, Long> cooldowns;

    /**
     * C'tor
     *
     * @param player
     */
    public EggCooldowns(String player) {
        this.player = player;
        this.cooldowns = new HashMap<String, Long>();
    }

    public String getPlayer() {
        return player;
    }

    /**
     * Gets the remaining cooldown of the egg in ms.
     *
     * @param egg
     * @return
     */
    public int getCooldown(SpecialEgg egg) {
        if (!cooldowns.containsKey(egg.getName())) {
            return 0;
        }

        int cd = (int) (cooldowns.get(egg.getName()) - System.currentTimeMillis());
        if (cd < 0) {
            cd = 0;
            cooldowns.remove(egg.getName());
        }
        return cd;
    }

    /**
     * Starts the cooldown for the given egg.
     *
     * @param egg
     */
    public void startCooldown(SpecialEgg egg) {
        cooldowns.put(egg.getName(), System.currentTimeMillis() + (egg.getCooldown() * 1000));
    }

}
