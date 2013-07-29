package net.new_liberty.nltweaks;

import net.new_liberty.nltweaks.tweak.ChatColorCommands;
import net.new_liberty.nltweaks.tweak.CreeperChest;
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
    @Override
    public void onEnable() {
        addTweak(new ChatColorCommands(this));
        addTweak(new CreeperChest(this));
        addTweak(new NerfStrengthPots(this));
        addTweak(new NoEnderpearls(this));
        addTweak(new NoInvisibilityPots(this));
        addTweak(new NoTNTMinecart(this));
        addTweak(new StaffList(this));
        addTweak(new VHomeInformer(this));
    }

    private void addTweak(Tweak tweak) {
        Bukkit.getPluginManager().registerEvents(tweak, this);
        tweak.onEnable();
    }
}
