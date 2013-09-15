/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nltweaks.tweak.specialeggs;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
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

    protected SpecialEggs ea = null;

    protected String description = "Does cool stuff.";

    protected EntityType eggType = EntityType.BAT;

    protected boolean allowInCombat = true;

    /**
     * Cooldown of the egg in seconds.
     */
    protected int cooldown = 0;

    protected boolean useInNoPvPZone = true;

    protected SpecialEgg(String name) {
        this.name = name;
    }

    public void initialize(SpecialEggs ea) {
        if (this.ea == null) {
            this.ea = ea;
        }
        System.out.println(this.ea);
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
        if (egg == null) {
            return false;
        }
        return egg.getType() == Material.MONSTER_EGG
                && egg.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(egg.getItemMeta().getDisplayName()).equals(name);
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
     * Checks if this egg can be used, and if it can, apply the cooldowns etc.
     *
     * @param p
     * @return
     */
    protected boolean checkCanUse(Player p) {
        if (!allowInCombat && ea.isInCombat(p)) {
            p.sendMessage(ChatColor.RED + "You can't use this egg while in combat.");
            return false;
        }

        if (!useInNoPvPZone
                && !ea.getWg().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation()).allows(DefaultFlag.PVP)) {
            p.sendMessage(ChatColor.RED + "You can't use this egg in a no-PvP zone.");
            return false;
        }

        EggCooldowns cds = ea.getCooldowns(p.getName());
        int cd = cds.getCooldown(this);

        if (cd > 0) {
            p.sendMessage(ChatColor.RED + "This egg is currently on cooldown for another " + ((int) Math.ceil(cd / 1000)) + " seconds.");
            return false;
        }

        // Can use
        cds.startCooldown(this);
        return true;
    }

}
