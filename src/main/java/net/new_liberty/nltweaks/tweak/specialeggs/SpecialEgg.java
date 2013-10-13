/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nltweaks.tweak.specialeggs;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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

    /**
     * Allow using this egg in a no PvP zone.
     */
    protected boolean useInNoPvPZone = true;

    /**
     * Allow using this egg in a no build zone.
     */
    protected boolean useInNoBuildZone = true;

    /**
     * Uses the player's location when checking if the egg can be used.
     */
    protected boolean usePlayerForLocationCheck = true;

    protected SpecialEgg(String name) {
        this.name = name;
    }

    public void initialize(SpecialEggs ea) {
        if (this.ea == null) {
            this.ea = ea;
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
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
    public ItemStack create(int amt) {
        ItemStack r = new ItemStack(Material.MONSTER_EGG, amt);
        r.setDurability(eggType.getTypeId());

        ItemMeta m = r.getItemMeta();
        m.setDisplayName(ChatColor.RESET + name);
        m.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.YELLOW + description));
        m.addEnchant(Enchantment.DURABILITY, 1, true);
        r.setItemMeta(m);
        m.removeEnchant(Enchantment.DAMAGE_ALL);

        return r;
    }

    /**
     * Gets the egg cooldown of the given player.
     *
     * @param player
     * @return
     */
    public int getCd(String player) {
        return ea.getCooldowns(player).getCooldown(this);
    }

    /**
     * Gets the egg cooldown of the given player.
     *
     * @param player
     * @return
     */
    public int getCd(Player player) {
        return getCd(player.getName());
    }

    /**
     * Checks if this egg can be used, and if it can, apply the cooldowns etc.
     *
     * @param p
     * @return
     */
    public boolean checkCanUse(Player p) {
        if (!allowInCombat && ea.isInCombat(p)) {
            p.sendMessage(ChatColor.RED + "You can't use this egg while in combat.");
            return false;
        }

        if (usePlayerForLocationCheck && !canUseAt(p, p.getLocation())) {
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

    /**
     * Checks if the player can use the egg at the given location. This is
     * useful for thrown eggs or eggs that create blocks.
     *
     * @param p
     * @param l
     * @return
     */
    public boolean canUseAt(Player p, Location l) {
        if (!useInNoPvPZone
                && !ea.getWg().getRegionManager(l.getWorld()).getApplicableRegions(p.getLocation()).allows(DefaultFlag.PVP)) {
            p.sendMessage(ChatColor.RED + "You can't use this egg in a no-PvP zone.");
            return false;
        }

        if (!useInNoBuildZone && !ea.getWg().canBuild(p, l)) {
            p.sendMessage(ChatColor.RED + "You cannot use this egg in a protected area.");
            return false;
        }

        return true;
    }

}
