package me.alexanderrebello.clockgui.listeners;

import me.alexanderrebello.clockgui.menus.TimeMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TimeStoneListener  implements Listener {

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
        if (!item.getItemMeta().getDisplayName().equalsIgnoreCase(TimeMenu.INVENTORY_ITEM_TITLE)) return;

        // show the menu to the player
        Player p = (Player) e.getPlayer();
        new TimeMenu(p);

        // prevent the player from using the stone like a normal item
        e.setCancelled(true);

        return;
    }
}
