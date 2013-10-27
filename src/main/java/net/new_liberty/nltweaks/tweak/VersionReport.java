/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nltweaks.tweak;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import net.new_liberty.nltweaks.Tweak;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 *
 * @author simplyianm
 */
public class VersionReport extends Tweak implements CommandExecutor {

    @Override
    public void onEnable() {
        plugin.getCommand("versionreport").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nl.admin")) {
            sender.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Please specify an output file name. Usage: /versionreport <filename>");
            return true;
        }

        String reportName = args[0];

        StringBuilder rb = new StringBuilder("{");

        // General server info
        rb.append("\"info\":");
        rb.append("{\"name\":\"").append(reportName).append("\",");
        rb.append("\"serverVersion\":\"").append(plugin.getServer().getVersion()).append("\",");
        rb.append("\"bukkitVersion\":\"").append(plugin.getServer().getBukkitVersion()).append("\"}");
        rb.append(",");

        // Plugin info
        rb.append("\"plugins\":[");
        List<String> plugins = new ArrayList<String>();
        for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
            PluginDescriptionFile pdf = p.getDescription();

            StringBuilder pl = new StringBuilder("{");
            pl.append("{");
            pl.append("\"name\":\"").append(pdf.getName()).append("\",");
            pl.append("\"version\":\"").append(pdf.getVersion()).append("\"");
            pl.append("}");
            plugins.add(pl.toString());
        }
        rb.append(Joiner.on(",").join(plugins));
        rb.append("]");

        rb.append("}");

        File dir = new File(plugin.getDataFolder(), "reports/");
        dir.mkdirs();
        File file = new File(dir, reportName + ".jar");
        file.delete();

        PrintWriter out = null;
        try {
            file.createNewFile();
            out = new PrintWriter(file);

            out.println(rb.toString());
        } catch (IOException ex) {
            sender.sendMessage(ChatColor.RED + "Error saving the version file!");
        } finally {
            out.close();
        }

        return true;
    }

}
