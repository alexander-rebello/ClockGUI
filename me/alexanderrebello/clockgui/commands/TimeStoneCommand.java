package me.alexanderrebello.clockgui.commands;

import me.alexanderrebello.clockgui.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TimeStoneCommand implements TabExecutor {

    private final FileConfiguration config;
    private final Main main;

    public TimeStoneCommand(Main main) {
        this.main = main;
        this.config = main.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        String error = null;
        String option = (args.length == 0) ? "" : args[0];

        switch (option) {
            case "":
                // prevent console trying to get a time stone
                if(!(commandSender instanceof Player)) {
                    error = "This command can only be executed by a player!";
                } else {
                    this.giveItem(commandSender);
                }
                break;
            case "remove":
                error = removeItem(args);
                if (error == null) commandSender.sendMessage(ChatColor.GREEN + "You successfully removed the item in slot " + args[1]);
                break;
            case "add":
                error = addItem(args);
                if (error == null) commandSender.sendMessage(ChatColor.GREEN + "You successfully added the item " + args[1]);
                break;
            default:
                error = "First argument must be empty, 'add' or 'remove'";
        }

        if (error != null) {
            commandSender.sendMessage(ChatColor.RED + error);
            return false;
        } else return true;
    }

    private void giveItem(CommandSender commandSender) {
        ItemStack timeStone = new ItemStack(Material.EMERALD, 1);
        ItemMeta timeStoneMeta = timeStone.getItemMeta();
        timeStoneMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.config.getString("item-name")));
        timeStoneMeta.addEnchant(Enchantment.VANISHING_CURSE, 10, true);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("With this mystical");
        lore.add("stone you may");
        lore.add("control time itself!");
        timeStoneMeta.setLore(lore);

        timeStone.setItemMeta(timeStoneMeta);

        Player p = (Player) commandSender;
        p.getInventory().addItem(timeStone);
    }

    @Nullable
    private String addItem(String[] args) {
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

        if (position < 0 || position >= 54) return "<position> must be a number between 0 and 53!";

        if (this.main.timeMenu.times.containsKey(position))  return "Already an item at given position! (First slot=0)";

        String error = this.main.timeMenu.add(args[1], time, material, position);

        if (error == null) this.main.createMenu();

        return error;
    }

    @Nullable
    private String removeItem(String[] args) {
        if (args.length != 2) return "Usage: /timestone remove <position>";

        // check if position is valid
        int position;
        try {
            position = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return "<position> must be a number!";
        }

        if (position < 0 || position >= 54) return "<position> must be a number between 0 and 53!";

        if (!this.main.timeMenu.times.containsKey(position))  return "No item at given position! (First slot=0)";

        String error = this.main.timeMenu.remove(position);

        if (error == null) this.main.createMenu();

        return error;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                completions.add("add");
                completions.add("remove");
                break;
            default:
                completions = null;
        }

        return completions;
    }
}














