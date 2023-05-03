package me.alexanderrebello.clockgui.menus;

import me.alexanderrebello.clockgui.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TimeMenu {

    public HashMap<Integer, Integer> times = new HashMap<Integer, Integer>();
    public ItemStack[] inventory;
    public Material[] materials;
    public String[] titles, titlesWithPrefix;
    public int invSize;
    public String menuTitle = "";
    private Connection connection;

    public TimeMenu(Connection connection, ResultSet resultSet, boolean addRow, String menuTitle, String itemPrefix) throws SQLException {
        this.connection = connection;
        this.menuTitle = menuTitle;

        // calculate correct size for inventory
        if (resultSet.last()) {
            int maxPos = resultSet.getInt("position") + 1;
            this.invSize = maxPos + (9 - maxPos % 9);
            if (addRow && this.invSize < 45) this.invSize += 9;
        } else {
            this.invSize = 9;
            Main.log("No items to display");
        }

        // create lists to store inventory data
        this.inventory = new ItemStack[this.invSize];
        this.materials = new Material[this.invSize];
        this.titles = new String[this.invSize];
        this.titlesWithPrefix = new String[this.invSize];

        // create placeholder items
        ItemStack placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        placeholderMeta.setDisplayName(" ");
        placeholder.setItemMeta(placeholderMeta);

        // fill inventory with placeholders
        for (int i=0; i < this.invSize; i++) inventory[i] = placeholder;

        // add items from the database
        if (resultSet.first()) do {

            int index = resultSet.getInt("position");

            this.materials[index] = Material.getMaterial(resultSet.getString("material").toUpperCase());

            if (this.materials[index] == null) this.materials[index] = Material.CLOCK;

            this.titles[index] = resultSet.getString("title");

            this.titlesWithPrefix[index] = itemPrefix + this.titles[index];

            // create item
            ItemStack item = new ItemStack(this.materials[index], 1);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(this.titlesWithPrefix[index]);
            item.setItemMeta(itemMeta);

            // add item
            this.times.put(index, resultSet.getInt("time"));
            this.inventory[index] = item;

        } while (resultSet.next());
    }


    public void showMenu(Player p) {
        // create the inventory later shown to the player
        Inventory inv = Bukkit.createInventory(p, this.invSize, this.menuTitle);

        // fill inventory with placeholder items
        inv.setContents(this.inventory);

        // show inventory to player
        p.openInventory(inv);
    }

    public int getIndexOfItem(ItemStack item) {
        return ArrayUtils.indexOf(this.inventory, item);
    }

    @Nullable
    public String add(String title, int time, Material material, int position) {
        try {
            String query = "INSERT INTO `time_gui` (`position`, `time`, `material`, `title`) VALUES (?,?,?,?)";
            PreparedStatement prepStmt = this.connection.prepareStatement(query);
            prepStmt.setString(4, title);
            prepStmt.setInt(2, time);
            prepStmt.setString(3, material.toString());
            prepStmt.setInt(1, position);
            prepStmt.executeQuery();
        } catch (SQLException e) {
            Main.log(e.getMessage(), Level.SEVERE);
            return "Could not add item. See log for more information";
        }
        return null;
    }

    @Nullable
    public String remove(int position) {
        try {
            String query = "DELETE FROM `time_gui` WHERE `position` = ?";
            PreparedStatement prepStmt = this.connection.prepareStatement(query);
            prepStmt.setInt(1, position);
            prepStmt.executeQuery();
        } catch (SQLException e) {
            Main.log(e.getMessage(), Level.SEVERE);
            return "Could not remove item. See log for more information";
        }
        return null;
    }


}
