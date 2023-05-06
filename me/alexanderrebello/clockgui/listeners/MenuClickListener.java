package me.alexanderrebello.clockgui.listeners;

import me.alexanderrebello.clockgui.Main;
import me.alexanderrebello.clockgui.menus.TimeMenu;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuClickListener implements Listener {

    private final Main main;

    public MenuClickListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;

        if (!(e.getWhoClicked() instanceof Player p)) return;

        // get current menu
        TimeMenu timeMenu = this.main.timeMenu;

        // check if time gui is open
        if (!e.getView().getTitle().equalsIgnoreCase(timeMenu.menuTitle)) return;

        // prevent player from taking the clock
        e.setCancelled(true);

        // check if clicked item is a clock
        ItemStack item = e.getCurrentItem();
        ItemMeta itemMeta = item.getItemMeta();

        int i = ArrayUtils.indexOf(timeMenu.inventory, item);

        if (
                i == -1 ||
                item.getType() != timeMenu.materials[i] ||
                itemMeta == null ||
                !itemMeta.getDisplayName().equals(timeMenu.itemPrefix + timeMenu.titles[i])
        ) return;

        World w = p.getWorld();

        int time = timeMenu.times.get(i);
        String title = timeMenu.titles[i];

        w.setTime(time);

        // announce time change
        for (Player player : w.getPlayers())
        {
            String itemTitle = this.main.getConfig().getString("item-name");
            if (itemTitle == null) itemTitle = "Item";
            else itemTitle = ChatColor.translateAlternateColorCodes('&', itemTitle);
            player.sendMessage(p.getDisplayName() + ChatColor.WHITE + " used " + itemTitle + ChatColor.WHITE + " and changed the time to " + ChatColor.GOLD + title);
        }
    }

}
