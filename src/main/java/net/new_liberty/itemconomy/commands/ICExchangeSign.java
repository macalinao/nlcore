/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.commands;

import java.util.HashMap;
import java.util.Map;
import net.new_liberty.itemconomy.Itemconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Creates an exchange sign.
 */
public class ICExchangeSign implements CommandExecutor, Listener {

    private Map<String, PotentialSign> pending = new HashMap<String, PotentialSign>();

    private final Itemconomy ic;

    public ICExchangeSign(Itemconomy ic) {
        this.ic = ic;
        ic.addListener(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to perform this command.");
            return true;
        }

        Player p = (Player) sender;
        if (!p.hasPermission("itemconomy.admin")) {
            p.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
            return true;
        }

        if (args.length < 2) {
            p.sendMessage(ChatColor.RED + "Usage: /icexchangesign <buy/sell> <amt>");
            return true;
        }

        boolean buy = args[0].toLowerCase().equals("buy");
        boolean sell = args[0].toLowerCase().equals("sell");

        if (!buy && !sell) {
            p.sendMessage(ChatColor.RED + "You can only have 'buy' or 'sell' signs. '" + args[0] + "' is an invalid sign type.");
            return true;
        }

        int amt = 0;
        try {
            amt = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            p.sendMessage(ChatColor.RED + "Amount '" + amt + "' not a number.");
            return true;
        }

        PotentialSign s = new PotentialSign(buy, amt);
        pending.put(p.getName(), s);
        p.sendMessage(ChatColor.YELLOW + "Right-click a sign to turn it into an exchange sign.");
        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        PotentialSign s = pending.get(e.getPlayer().getName());
        if (s == null) {
            return;
        }

        Block b = e.getClickedBlock();
        if (b.getType() != Material.SIGN) {
            pending.remove(e.getPlayer().getName());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Pending exchange sign creation cancelled.");
            return;
        }

        // Create sign
        ic.getExchange().getSigns().createSign(b, s.isBuy(), s.getAmt());
        ic.getExchange().getSigns().save();

        pending.remove(e.getPlayer().getName());
        e.getPlayer().sendMessage(ChatColor.YELLOW + "Exchange sign to "
                + (s.isBuy() ? "buy" : "sell") + " " + s.getAmt() + " emeralds created.");
    }

    private static class PotentialSign {

        private boolean buy;

        private int amt;

        public PotentialSign(boolean buy, int amt) {
            this.buy = buy;
            this.amt = amt;
        }

        public boolean isBuy() {
            return buy;
        }

        public boolean isSell() {
            return !buy;
        }

        public int getAmt() {
            return amt;
        }

    }
}
