/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nltweaks.tweak.eggarsenal;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents an egg that does more than just spawn a mob.
 */
public abstract class SpecialEgg implements Listener {

    private final String name;

    protected EggArsenal ea = null;

    protected String description = "Does cool stuff.";

    protected EntityType eggType = EntityType.BAT;

    protected boolean allowInCombat = true;

    /**
     * Cooldown of the egg in seconds.
     */
    protected int cooldown = 0;

    protected SpecialEgg(String name) {
        this.name = name;
    }

    public void initialize(EggArsenal ea) {
        if (ea == null) {
            this.ea = ea;
        }
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }

    /**
     * Checks if the given ItemStack is an instance of the egg.
     *
     * @param egg
     * @return
     */
    public boolean isInstance(ItemStack egg) {
        return egg.getType() == Material.MONSTER_EGG && egg.getItemMeta().getDisplayName().equals(name);
    }

    /**
     * Creates one of these eggs.
     *
     * @return
     */
    public ItemStack create() {
        ItemStack r = new ItemStack(Material.MONSTER_EGG, 1);
        r.setDurability(eggType.getTypeId());

        ItemMeta m = r.getItemMeta();
        m.setDisplayName(ChatColor.RESET + name);
        m.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.YELLOW + description));
        r.setItemMeta(m);

        return r;
    }

    /**
     * Checks if this egg can be used
     *
     * @param p
     * @return
     */
    protected boolean checkCanUse(Player p) {
        if (!allowInCombat && ea.isInCombat(p)) {
            p.sendMessage(ChatColor.RED + "You can't use this egg while in combat.");
            return false;
        }

        int cd = ea.getCooldowns(p.getName()).getCooldown(this);
        if (cd > 0) {
            p.sendMessage(ChatColor.RED + "This egg is currently on cooldown for another " + (Math.ceil(cd / 1000)) + " seconds.");
            return false;
        }

        return true;
    }

}
