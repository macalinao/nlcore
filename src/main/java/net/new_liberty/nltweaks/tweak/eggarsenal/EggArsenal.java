package net.new_liberty.nltweaks.tweak.eggarsenal;

import java.util.HashMap;
import java.util.Map;
import net.new_liberty.nltweaks.Tweak;
import net.new_liberty.nltweaks.tweak.eggarsenal.eggs.BlinkEgg;
import org.bukkit.Bukkit;

public class EggArsenal extends Tweak {

    private Map<String, SpecialEgg> eggs = new HashMap<String, SpecialEgg>();

    @Override
    public void onEnable() {
        addEgg(new BlinkEgg());
    }

    private void addEgg(SpecialEgg egg) {
        eggs.put(egg.getName(), egg);
        Bukkit.getPluginManager().registerEvents(egg, plugin);
    }
}
