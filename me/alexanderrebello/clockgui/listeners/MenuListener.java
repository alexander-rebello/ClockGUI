package me.alexanderrebello.clockgui.listeners;

import me.alexanderrebello.clockgui.menus.TimeMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    @EventHandler
    public void  onMenuClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;

        if (!(e.getWhoClicked() instanceof Player)) return;

        // check if time gui is open
        if (!e.getView().getTitle().equalsIgnoreCase(TimeMenu.MENU_TITLE)) return;

        // prevent player from taking the clock
        e.setCancelled(true);

        // check if clicked item is a clock
        ItemStack item = e.getCurrentItem();
        if (item.getType() != Material.CLOCK) return;
        Bukkit.getLogger().info("5");

        Player p = (Player) e.getWhoClicked();
        World w = p.getWorld();

        String time = item.getItemMeta().getDisplayName().replace(TimeMenu.ITEM_TITLE_PREFIX, "");

        // check if item is part of the clock inventory and change time
        switch (time) {
            case TimeMenu.DAWN:
                w.setTime(6000);
                break;
            case TimeMenu.NOON:
                w.setTime(13000);
                break;
            case TimeMenu.DUSK:
                w.setTime(20000);
                break;
            default:
                return;
        }

        // announce time change
        for (Player player : w.getPlayers())
        {
            player.sendMessage(p.getDisplayName() + ChatColor.WHITE + " changed the time to " + ChatColor.GOLD + time);
        }
    }

}
