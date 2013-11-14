/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.bankchests;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Bank chest listener
 */
public class BCListener implements Listener {

    private final BankChests b;

    public BCListener(BankChests b) {
        this.b = b;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() != Material.ENDER_CHEST) {
            return;
        }

        Player p = e.getPlayer();
        if (!p.hasPermission("bankchests.admin")) {
            p.sendMessage(ChatColor.RED + "You're not allowed to place Ender Chests.");
            e.setCancelled(true);
            return;
        }

        p.sendMessage(ChatColor.YELLOW + "You have placed an ender chest (bank chest).");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.ENDER_CHEST) {
            return;
        }

        Player p = e.getPlayer();
        if (!p.hasPermission("bankchests.admin")) {
            e.setCancelled(true);
            return;
        }

        p.sendMessage(ChatColor.YELLOW + "You have broken a bank chest.");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (e.getClickedBlock().getType() != Material.ENDER_CHEST) {
            return;
        }

        Player p = e.getPlayer();
        BankChest c = b.getChests().getChest(p.getName());
        p.openInventory(c.getInventory());
    }

}
