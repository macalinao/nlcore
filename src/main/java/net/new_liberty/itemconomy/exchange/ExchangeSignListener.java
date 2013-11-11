/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

import net.new_liberty.itemconomy.CurrencyInventory;
import net.new_liberty.nlcore.player.NLPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        NLPlayer p = new NLPlayer(e.getPlayer());
        double balance = p.balance();
        CurrencyInventory inv = p.getEmeraldInventory();
    }

}
