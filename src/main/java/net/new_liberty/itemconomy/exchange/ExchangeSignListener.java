/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

import net.milkbowl.vault.chat.Chat;
import net.new_liberty.itemconomy.CurrencyInventory;
import net.new_liberty.nlcore.player.NLPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener for Exchange signs
 */
public class ExchangeSignListener implements Listener {

    private final Exchange x;

    public ExchangeSignListener(Exchange x) {
        this.x = x;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block b = e.getClickedBlock();
        if (b == null) {
            return;
        }
        if (b.getType() != Material.SIGN) {
            return;
        }

        ExchangeSign s = x.getSigns().getSign(b);
        if (s == null) {
            return;
        }

        Player p = e.getPlayer();
        NLPlayer n = new NLPlayer(p);
        double balance = n.balance();
        double price = s.price();
        if (price > balance) {
            p.sendMessage(ChatColor.RED + "You don't have enough civs to buy this many emeralds.");
            return;
        }

        CurrencyInventory c = n.getEmeraldInventory();
        if (!c.add(s.getAmt())) {
            p.sendMessage(ChatColor.RED + "You don't have enough space in your inventory for this many emeralds.");
            return;
        }

        n.withdraw(price);
        p.sendMessage(ChatColor.YELLOW + "You have purchased " + s.getAmt() + " emeralds for " + price + " civs.");
    }

}
