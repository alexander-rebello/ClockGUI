package me.alexanderrebello.clockgui.listeners;

import me.alexanderrebello.clockgui.Main;
import me.alexanderrebello.clockgui.menus.TimeMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    private FileConfiguration config;
    private Main main;

    public MenuListener(Main main) {
        this.config = main.getConfig();
        this.main = main;
    }

    @EventHandler
    public void  onMenuClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;

        if (!(e.getWhoClicked() instanceof Player)) return;

        // check if time gui is open
        if (!e.getView().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', this.config.getString("menu-title")))) return;

        // prevent player from taking the clock
        e.setCancelled(true);

        // get current menu
        TimeMenu timeMenu = this.main.timeMenu;

        // check if clicked item is a clock
        ItemStack item = e.getCurrentItem();
        int i = timeMenu.getIndexOfItem(item);

        if (item.getType() != timeMenu.materials[i]) return;

        Player p = (Player) e.getWhoClicked();
        World w = p.getWorld();

        int time = timeMenu.times.get(i);
        String title = timeMenu.titles[i];

        w.setTime(time);

        // announce time change
        for (Player player : w.getPlayers())
        {
            player.sendMessage(p.getDisplayName() + ChatColor.WHITE + " used " + ChatColor.translateAlternateColorCodes('&', this.config.getString("item-name")) + ChatColor.WHITE + " and changed the time to " + ChatColor.GOLD + title);
        }
    }

}
