package net.new_liberty.nltweaks.tweak;

import java.util.Arrays;
import java.util.List;
import net.new_liberty.nltweaks.NLTweaks;
import net.new_liberty.nltweaks.Tweak;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands to allow changing of chat colors.
 */
public class ChatColorCommands extends Tweak {
    private static final String OK_RESPONSE = "OK";

    /**
     * Restricted solid prefix colours
     */
    private static final List<Character> rsp = Arrays.asList('c', 'e', 'd', 'a', '5', '0');

    public ChatColorCommands(NLTweaks plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        plugin.getCommand("setcolors").setExecutor(new SetColorsCommand());
        plugin.getCommand("setchatcolor").setExecutor(new SetChatColorCommand());
    }

    /**
     * Checks suffix validity
     *
     * @param suffix
     * @return
     */
    private static boolean equalsOne(char c, char... cv) {
        for (char character : cv) {
            if (c == character) {
                return true;
            }
        }
        return false;
    }

    /**
     * Represents a command that changes a chat color.
     */
    public static abstract class ChatColorCommand implements CommandExecutor {
        private final String type;

        private final String permission;

        public ChatColorCommand(String type, String permission) {
            this.type = type;
            this.permission = permission;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED
                        + "Only players have prefixes/ uffixes!");
                return true;
            }

            if (!sender.hasPermission(permission)) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You must specify a " + type.toLowerCase() + ".");
                return true;
            }

            String value = args[0].toLowerCase();

            String rank = null;
            if (sender.hasPermission("chatcolorcommands.ranks.guardian")) {
                rank = "Guardian";
            } else if (sender.hasPermission("chatcolorcommands.ranks.champion")) {
                rank = "Champion";
            } else {
                sender.sendMessage(ChatColor.RED + "Your rank is unknown.");
                return true;
            }

            String validation = validate(rank, value);
            if (validation.equals(OK_RESPONSE)) {
                execute(sender, parse(rank, value));
            } else {
                sender.sendMessage(ChatColor.RED + "Error: " + validation);
            }
            return true;
        }

        public abstract String validate(String rank, String value);

        public abstract String parse(String rank, String value);

        public abstract void execute(CommandSender sender, String value);
    }

    public static class SetColorsCommand extends ChatColorCommand {
        public SetColorsCommand() {
            super("Colors", "chatcolorcommands.setcolors");
        }

        @Override
        public String validate(String rank, String prefix) {
            int required = rank.length() + 1;
            if (prefix.length() != required) {
                return "Your prefix needs to be " + required + " characters long.";
            }

            if (prefix.contains("5") || prefix.contains("0")) {
                return "Invalid prefix (violates restricted prefix colors)";
            }

            char lastCode = prefix.charAt(prefix.length() - 1);
            if (equalsOne(lastCode, 'e', 'c', 'a', '5', 'd', '0')) {
                return "Invalid prefix (violates restricted name colors)";
            }

            char last = ' ';
            for (int i = 0; i < prefix.length(); i++) {
                char cur = prefix.charAt(i);
                if (rsp.contains(last) && rsp.contains(cur)) {
                    return "Invalid prefix (violates restricted solid prefix colors)";
                }

                last = cur;
            }

            return OK_RESPONSE;
        }

        @Override
        public String parse(String rank, String value) {
            StringBuilder b = new StringBuilder("[");
            char[] colors = value.toCharArray();
            char[] rankChars = rank.toCharArray();

            for (int i = 0; i < rankChars.length; i++) {
                b.append('&').append(colors[i]).append(rankChars[i]);
            }

            return b.append("&f]&").append(colors[colors.length - 1]).toString();
        }

        @Override
        public void execute(CommandSender sender, String prefix) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user "
                    + sender.getName() + " prefix set " + prefix);
            sender.sendMessage("Your colors have been set to: " + ChatColor.translateAlternateColorCodes('&', prefix));
        }
    }

    /**
     * Command to set the chat color.
     */
    public static class SetChatColorCommand extends ChatColorCommand {
        public SetChatColorCommand() {
            super("Chat Color", "chatcolorcommands.setchatcolor");
        }

        @Override
        public String validate(String rank, String value) {
            if (value.length() != 1) {
                return "Suffixes are only one colour!";
            }

            char suffix = value.charAt(0);
            if (equalsOne(suffix, 'e', 'c', 'a', '5', 'd', '0', '4')) {
                return "Invalid suffix (violates restricted chat colors)";
            }

            ChatColor color = ChatColor.getByChar(suffix);
            if (color == null) {
                return "Unknown color.";
            }

            return OK_RESPONSE;
        }

        @Override
        public String parse(String rank, String value) {
            return "&" + ChatColor.getByChar(value).getChar();
        }

        @Override
        public void execute(CommandSender sender, String suffix) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user "
                    + sender.getName() + " suffix set " + suffix);
            sender.sendMessage("Changed chat color to: " + ChatColor.translateAlternateColorCodes('&', suffix) + "this color");
        }
    }
}
