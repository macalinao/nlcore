package net.new_liberty.nltweaks;

import java.util.logging.Level;

import net.new_liberty.nltweaks.tweak.ChatColorCommands;
import net.new_liberty.nltweaks.tweak.EasySpawners;
import net.new_liberty.nltweaks.tweak.eggarsenal.EggArsenal;
import net.new_liberty.nltweaks.tweak.MobTamer;
import net.new_liberty.nltweaks.tweak.NerfStrengthPots;
import net.new_liberty.nltweaks.tweak.NoEnderpearls;
import net.new_liberty.nltweaks.tweak.NoInvisibilityPots;
import net.new_liberty.nltweaks.tweak.NoTNTMinecart;
import net.new_liberty.nltweaks.tweak.StaffList;
import net.new_liberty.nltweaks.tweak.VHomeInformer;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This plugin contains all of the tweaks to New Liberty that don't warrant
 * their own plugin.
 */
public class NLTweaks extends JavaPlugin implements Listener {

    private static NLTweaks instance;

    @Override
    public void onEnable() {
        instance = this;

        addTweak(new ChatColorCommands());
        addTweak(new EasySpawners());
        addTweak(new EggArsenal());
        addTweak(new MobTamer());
        addTweak(new NerfStrengthPots());
        addTweak(new NoEnderpearls());
        addTweak(new NoInvisibilityPots());
        addTweak(new NoTNTMinecart());
        addTweak(new StaffList());
        addTweak(new VHomeInformer());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void addTweak(Tweak tweak) {
        tweak.initialize(this);
        Bukkit.getPluginManager().registerEvents(tweak, this);
        tweak.onEnable();
        tweak.getLogger().log(Level.INFO, "Tweak enabled.");
    }

    public static NLTweaks getInstance() {
        return instance;
    }

}
