package me.alexanderrebello.clockgui.listeners;

import me.alexanderrebello.clockgui.Main;
import me.alexanderrebello.clockgui.menus.TimeMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TimeStoneListener  implements Listener {

    private TimeMenu timeMenu;
    private FileConfiguration config;

    public TimeStoneListener(Main main) {
        this.config = main.getConfig();
        this.timeMenu = main.timeMenu;
    }

    @EventHandler
    public void onTimeStoneClick(PlayerInteractEvent e){

        if( e.getAction() != Action.LEFT_CLICK_BLOCK &&
            e.getAction() != Action.LEFT_CLICK_AIR &&
            e.getAction() != Action.RIGHT_CLICK_BLOCK &&
            e.getAction() != Action.RIGHT_CLICK_AIR) return;


        if (e.getItem() == null) return;

        ItemStack item = e.getItem();

        // check if clicked item is really emerald
        if (item.getType() != Material.EMERALD) return;

        // check if clicked item is really the time stone
        if (!item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', this.config.getString("item-name")))) return;

        // prevent the player from using the stone like a normal item
        e.setCancelled(true);

        // show the menu to the player
        Player p = (Player) e.getPlayer();
        this.timeMenu.showMenu(p);

        return;
    }
}
