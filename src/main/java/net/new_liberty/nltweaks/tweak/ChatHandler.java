/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nltweaks.tweak;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.milkbowl.vault.chat.Chat;
import net.new_liberty.nltweaks.Tweak;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author simplyianm
 */
public class ChatHandler extends Tweak {

    private Chat chat;

    @Override
    public void onEnable() {
        setupChat();
    }

    public boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();

        String fprefix = "";
        FPlayer player = FPlayers.i.get(p);
        if (player.hasFaction()) {
            fprefix = Conf.chatTagReplaceString;
        }
        fprefix = "[" + fprefix + "]";
        String prefix = ChatColor.translateAlternateColorCodes('&', chat.getPlayerPrefix(p));
        String suffix = ChatColor.translateAlternateColorCodes('&', chat.getPlayerSuffix(p));

        String format = fprefix + prefix + " " + p.getName() + ChatColor.WHITE + ": " + suffix + "%2$s";
        event.setFormat(format);
    }

}
