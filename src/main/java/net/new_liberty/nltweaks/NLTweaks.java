package net.new_liberty.nltweaks;

import net.new_liberty.nltweaks.tweak.NerfStrengthPots;
import net.new_liberty.nltweaks.tweak.NoEnderpearls;
import net.new_liberty.nltweaks.tweak.NoTNTMinecart;
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
        addTweak(new NerfStrengthPots());
        addTweak(new NoEnderpearls());
        addTweak(new NoTNTMinecart());
        addTweak(new VHomeInformer());
    }

    private void addTweak(Tweak tweak) {
        Bukkit.getPluginManager().registerEvents(tweak, this);
    }
}
