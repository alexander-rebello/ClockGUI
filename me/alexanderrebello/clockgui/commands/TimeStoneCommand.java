package me.alexanderrebello.clockgui.commands;

import me.alexanderrebello.clockgui.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.*;

public class TimeStoneCommand implements TabExecutor {

    private final Main main;
    public final List<String> subcommands = new ArrayList<>();

    /**
     * @param main Main class of plugin
     */
    public TimeStoneCommand(@Nonnull Main main) {
        this.main = main;

        this.subcommands.add("reload");
        this.subcommands.add("give");
        this.subcommands.add("add");
        this.subcommands.add("remove");
        this.subcommands.add("help");
    }

    /**
     *
     * @param commandSender sender of command
     * @param command issued command
     * @param alias alias used
     * @param args command arguments
     * @return true if successful, otherwise false
     */
    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        String error = null;
        String option = (args.length == 0) ? "" : args[0];

        switch (option) {
            case "reload":
                if (args.length != 1) error = "Usage: /timestone reload";
                this.main.reloadConfig();
                this.main.createMenu();
                commandSender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config!");
                break;
            case "help":
                error = this.showHelp(args, commandSender);
                break;
            case "give":
                // prevent console trying to get a time stone
                if(!(commandSender instanceof Player)) {
                    error = "This command can only be executed by a player!";
                } else {
                    error = this.giveItem(args, commandSender);

                    String name = this.main.getConfig().getString("item-name");
                    if (name == null) name = "Item";
                    else name = ChatColor.translateAlternateColorCodes('&', name);

                    if (error == null) commandSender.sendMessage(ChatColor.GREEN + "The " + name + " was successfully given to you!");
                }
                break;
            case "remove":
                error = this.removeItem(args);
                if (error == null) commandSender.sendMessage(ChatColor.GREEN + "You successfully removed the item in slot " + args[1]);
                break;
            case "add":
                error = this.addItem(args);
                if (error == null) commandSender.sendMessage(ChatColor.GREEN + "You successfully added the item " + args[1]);
                break;
            default:
                error = "First argument must be 'reload', 'help', 'give', 'add' or 'remove'";
        }

        if (error != null) {
            commandSender.sendMessage(ChatColor.RED + error);
            return false;
        } else return true;
    }

    /**
     *
     * @param args command arguments
     * @param commandSender sender of command
     * @return error, null if no error
     */
    @Nullable
    private String showHelp(@Nonnull String[] args, @Nonnull CommandSender commandSender) {
        String option;
        if (args.length == 1) option = "*";
        else if (args.length == 2) option = args[1];
        else return "Usage: /timestone help [subcommand]";

        List<String> help = new ArrayList<>();

        help.add(" ");
        help.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Usage of /timestone:");

        // single command help answers
        switch (option) {
            case "*":
            case "reload":
                help.add(ChatColor.WHITE + "/timestone reload");
                help.add(ChatColor.WHITE + "Reload the config and get data from database");
                help.add(" ");
                if (!option.equals("*")) break;
            case "help":
                help.add(ChatColor.WHITE + "/timestone help [subcommand]");
                help.add(ChatColor.WHITE + "Get help for commands");
                help.add(" ");
                if (!option.equals("*")) break;
            case "give":
                help.add(ChatColor.WHITE + "/timestone give [player]");
                help.add(ChatColor.WHITE + "Give the item to a player or yourself when missing the argument");
                help.add(" ");
                if (!option.equals("*")) break;
            case "add":
                help.add(ChatColor.WHITE + "/timestone add <title> <time> <material> <position>");
                help.add(ChatColor.WHITE + "Add item to menu");
                help.add(" ");
                if (!option.equals("*")) break;
            case "remove":
                help.add(ChatColor.WHITE + "/timestone remove <position>");
                help.add(ChatColor.WHITE + "remove item from menu");
                help.add(" ");
                break;
            default:
                return "Subcommand '" + option + "' not found!";
        }

        for (String msg : help) {
            commandSender.sendMessage(msg);
        }

        return null;
    }

    /**
     *
     * @param args command arguments
     * @param commandSender sender of command
     * @return error, null if no error
     */
    @Nullable
    private String giveItem(@Nonnull String[] args, @Nonnull CommandSender commandSender) {
        Player p;
        if (args.length == 1) {
            p = (Player) commandSender;
        } else if (args.length == 2) {
            p = Bukkit.getPlayer(args[1]);
            if (p == null) return "Player '" + args[1] + "' not found!";
        } else return "Usage: /timestone give [player]";

        if (p.getInventory().firstEmpty() == -1) return "Inventory of " + p.getDisplayName() + " is full!";

        ItemStack timeStone = new ItemStack(Material.EMERALD, 1);
        ItemMeta timeStoneMeta = timeStone.getItemMeta();
        assert timeStoneMeta != null;

        String name = this.main.getConfig().getString("item-name");
        if (name == null) name = "Item";
        else name = ChatColor.translateAlternateColorCodes('&', name);

        timeStoneMeta.setDisplayName(name);
        timeStoneMeta.addEnchant(Enchantment.VANISHING_CURSE, 10, true);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("With this mystical");
        lore.add("stone you may");
        lore.add("control time itself!");
        timeStoneMeta.setLore(lore);

        timeStone.setItemMeta(timeStoneMeta);

        p.getInventory().addItem(timeStone);

        return null;
    }

    /**
     *
     * @param args command arguments
     * @return error, null if no error
     */
    @Nullable
    private String addItem(@Nonnull String[] args) {
        if (args.length != 5) return "Usage: /timestone add <title> <time> <material> <position>";

        // check if time is valid
        int time;
        try {
            time = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return "<time> must be a number!";
        }

        if (time < 0 || time > 24000) return "<time> must be a number between 0 and 24000!";

        // check if material is valid
        Material material = Material.matchMaterial(args[3]);
        if (material == null) return "<material> must be a valid minecraft material!";

        // check if position is valid
        int position;
        try {
            position = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            return "<position> must be a number!";
        }

        if (position < 0 || position > 53) return "<position> must be a number between 0 and 53!";

        if (this.main.timeMenu.times.containsKey(position))  return "Already an item at given position! (First slot=0)";

        return this.main.timeMenu.add(args[1], time, material, position);
    }

    /**
     *
     * @param args command arguments
     * @return error, null if no error
     */
    @Nullable
    private String removeItem(@Nonnull String[] args) {
        if (args.length != 2) return "Usage: /timestone remove <position>";

        // check if position is valid
        int position;
        try {
            position = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return "<position> must be a number!";
        }

        if (position < 0 || position > 53) return "<position> must be a number between 0 and 53!";

        if (!this.main.timeMenu.times.containsKey(position))  return "No item at given position! (First slot=0)";

        return this.main.timeMenu.remove(position);
    }

    /**
     *
     * @param commandSender sender of command
     * @param command issued command
     * @param alias alias used
     * @param args command arguments
     * @return List of suggestions
     */
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                completions = this.subcommands;
                break;
            case 2:
                if (args[0].equalsIgnoreCase("remove")) {
                    for (HashMap.Entry<Integer, Integer> entry : this.main.timeMenu.times.entrySet()) {
                        completions.add(Integer.toString(entry.getKey()));
                    }
                } else if (args[0].equalsIgnoreCase("give")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    completions = this.subcommands;
                }
                break;
            case 3:
                if (!args[0].equalsIgnoreCase("add")) break;

                for (int i=0; i<=24; i++) {
                    String s = Integer.toString(i*1000);
                    if (i==0) s = "   " + s;
                    if (i<10) s = " " + s;
                    completions.add(s);
                }

                break;
            case 4:
                if (!args[0].equalsIgnoreCase("add")) break;

                for(Material mat : Material.values()) {
                    completions.add("minecraft:" + mat.toString().toLowerCase());
                }

                break;
            case 5:
                if (!args[0].equalsIgnoreCase("add")) break;

                for (int i=0; i<=53; i++) {
                    String s = Integer.toString(i);
                    if (i<10) s = " " + s;
                    completions.add(s);
                }

                break;
        }

        if (completions.isEmpty()) return completions;

        List<String> filteredCompletions = new ArrayList<>();

        for(String str : completions){
            if(str.startsWith(args[args.length-1])) {
                filteredCompletions.add(str);
            }
        }

        Collections.sort(filteredCompletions);

        return filteredCompletions;
    }
}
