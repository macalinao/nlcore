package net.new_liberty.core;

import java.util.logging.Level;
import net.new_liberty.core.enderchestprotect.EnderChestProtect;
import net.new_liberty.core.itemconomy.Itemconomy;

import net.new_liberty.core.tweaks.ChatColorCommands;
import net.new_liberty.core.tweaks.EasySpawners;
import net.new_liberty.core.tweaks.HeadDrop;
import net.new_liberty.core.specialeggs.SpecialEggs;
import net.new_liberty.core.tweaks.MobTamer;
import net.new_liberty.core.tweaks.NerfStrengthPots;
import net.new_liberty.core.tweaks.NoEnderpearls;
import net.new_liberty.core.tweaks.NoInvisibilityPots;
import net.new_liberty.core.tweaks.NoTNTMinecart;
import net.new_liberty.core.tweaks.PortalUnstuck;
import net.new_liberty.core.tweaks.StaffList;
import net.new_liberty.core.tweaks.VHomeInformer;
import net.new_liberty.core.tweaks.VersionReport;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This plugin contains all of the tweaks to New Liberty that don't warrant
 * their own plugin.
 */
public class NLCore extends JavaPlugin implements Listener {

    private static NLCore instance;

    @Override
    public void onEnable() {
        instance = this;

        // Modules
        addModule(new EnderChestProtect());
        addModule(new Itemconomy());
        addModule(new SpecialEggs());

        // Tweaks
        addModule(new ChatColorCommands());
        addModule(new EasySpawners());
        addModule(new HeadDrop());
        addModule(new MobTamer());
        addModule(new NerfStrengthPots());
        addModule(new NoEnderpearls());
        addModule(new NoInvisibilityPots());
        addModule(new NoTNTMinecart());
        addModule(new PortalUnstuck());
        addModule(new StaffList());
        addModule(new VersionReport());
        addModule(new VHomeInformer());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void addModule(Module tweak) {
        tweak.initialize(this);
        Bukkit.getPluginManager().registerEvents(tweak, this);
        tweak.onEnable();
        tweak.getLogger().log(Level.INFO, "Tweak enabled.");
    }

    public static NLCore getInstance() {
        return instance;
    }

}
