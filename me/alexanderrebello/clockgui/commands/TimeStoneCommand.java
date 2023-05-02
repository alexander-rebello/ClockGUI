package me.alexanderrebello.clockgui.commands;

import me.alexanderrebello.clockgui.Main;
import me.alexanderrebello.clockgui.menus.TimeMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class TimeStoneCommand implements CommandExecutor {

    private FileConfiguration config;

    public TimeStoneCommand(Main main) {
        this.config = main.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // prevent console trying to get a time stone
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be executed by a player!");
            return false;
        }

        ItemStack timeStone = new ItemStack(Material.EMERALD, 1);
        ItemMeta timeStoneMeta = timeStone.getItemMeta();
        timeStoneMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.config.getString("item-name")));
        timeStoneMeta.addEnchant(Enchantment.VANISHING_CURSE, 10, true);

        ArrayList<String> lore = new ArrayList<String>();
        lore.add("With this mystical");
        lore.add("stone you may");
        lore.add("control time itself!");
        timeStoneMeta.setLore(lore);

        timeStone.setItemMeta(timeStoneMeta);

        Player p = (Player) commandSender;
        p.getInventory().addItem(timeStone);

        return true;
    }
}
