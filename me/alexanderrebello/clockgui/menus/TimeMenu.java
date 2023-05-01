package me.alexanderrebello.clockgui.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TimeMenu {

    public static final String MENU_TITLE = ChatColor.BLUE + "Clock GUI";
    public static final String INVENTORY_ITEM_TITLE = ChatColor.RED + "Dr. Strange's " + ChatColor.GREEN + "Time Stone";
    public static final String ITEM_TITLE_PREFIX = ChatColor.WHITE + "Click for " + ChatColor.GOLD;

    public static final String DAWN = "Dawn", NOON = "Noon", DUSK = "Dusk";

    public TimeMenu(Player p) {
        Bukkit.getLogger().info("8");
        // create the inventory later shown to the player
        Inventory inventory = Bukkit.createInventory(p, 27, this.MENU_TITLE);

        // create placeholder items
        ItemStack placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        placeholderMeta.setDisplayName(" ");
        placeholder.setItemMeta(placeholderMeta);

        // fill inventory with placeholder items
        for (int i=0; i < 27; i++) {
            inventory.setItem(i, placeholder);
        }

        // create dawn clock item
        ItemStack clockDawn = new ItemStack(Material.CLOCK, 6);
        ItemMeta clockDawnMeta = clockDawn.getItemMeta();
        clockDawnMeta.setDisplayName(this.ITEM_TITLE_PREFIX + this.DAWN);
        clockDawn.setItemMeta(clockDawnMeta);

        // create noon clock item
        ItemStack clockNoon = new ItemStack(Material.CLOCK, 13);
        ItemMeta clockNoonMeta = clockNoon.getItemMeta();
        clockNoonMeta.setDisplayName(this.ITEM_TITLE_PREFIX + this.NOON);
        clockNoon.setItemMeta(clockNoonMeta);

        // create dusk clock item
        ItemStack clockDusk = new ItemStack(Material.CLOCK, 20);
        ItemMeta clockDuskMeta = clockDusk.getItemMeta();
        clockDuskMeta.setDisplayName(this.ITEM_TITLE_PREFIX + this.DUSK);
        clockDusk.setItemMeta(clockDuskMeta);

        // add clocks to inventory
        inventory.setItem(11, clockDawn);
        inventory.setItem(13, clockNoon);
        inventory.setItem(15, clockDusk);

        p.openInventory(inventory);
    }
}
