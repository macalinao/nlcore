/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.horses.commands;

import net.new_liberty.horses.Horses;
import net.new_liberty.horses.OwnedHorse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Disowns your horse.
 */
public class DisownHorseCommand implements CommandExecutor {

    private final Horses h;

    public DisownHorseCommand(Horses h) {
        this.h = h;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "You didn't specify a horse to disown. Usage: /disownhorse <horse>");
            return true;
        }

        String callName = args[0];
        OwnedHorse o = h.getHorses().getHorse(player.getName(), callName);
        if (o == null) {
            player.sendMessage(ChatColor.RED + "You don't have a horse named '" + callName + "'.");
            return true;
        }

        o.delete();
        sender.sendMessage(ChatColor.RED + "You have disowned your horse '" + o.getCallName() + "'.");
        return true;
    }

}
