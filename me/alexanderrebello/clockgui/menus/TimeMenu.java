package me.alexanderrebello.clockgui.menus;

import me.alexanderrebello.clockgui.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.HashMap;
import java.util.logging.Level;

public class TimeMenu {

    public HashMap<Integer, Integer> times = new HashMap<>();
    public ItemStack[] inventory;
    public Material[] materials;
    public String[] titles;
    public int invSize;
    public final String menuTitle;
    public final String itemPrefix;
    private final Connection connection;
    public final ItemStack placeholder;

    /**
     *
     * @param connection the database connection for updating the data
     * @param resultSet the database data to display
     * @param addRow if an empty row should be added to the bottom or not
     * @param menuTitle in-game title for menu
     * @param itemPrefix the text shown before the name of the item
     * @throws SQLException if given resultSet is empty
     */
    public TimeMenu(@Nonnull Connection connection, @Nonnull ResultSet resultSet, boolean addRow, @Nonnull String menuTitle, @Nonnull String itemPrefix) throws SQLException {
        this.connection = connection;
        this.menuTitle = menuTitle;
        this.itemPrefix = itemPrefix;

        // calculate correct size for inventory
        if (resultSet.last()) {
            int maxPos = resultSet.getInt("position") + 1;
            this.invSize = maxPos + (9 - maxPos % 9);
            if (addRow && this.invSize < 45) this.invSize += 9;
            if (this.invSize > 54) this.invSize = 54;
        } else {
            this.invSize = 9;
            Main.log("No items to display");
        }

        // create placeholder items
        this.placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta placeholderMeta = this.placeholder.getItemMeta();
        assert placeholderMeta != null;
        placeholderMeta.setDisplayName(" ");
        this.placeholder.setItemMeta(placeholderMeta);

        // create lists to store inventory data
        this.inventory = new ItemStack[this.invSize];
        this.materials = new Material[this.invSize];
        this.titles = new String[this.invSize];

        // fill inventory with placeholders
        for (int i=0; i < this.invSize; i++) inventory[i] = placeholder;

        // add items from the database
        if (resultSet.first()) do {

            int index = resultSet.getInt("position");

            this.materials[index] = Material.getMaterial(resultSet.getString("material").toUpperCase());

            if (this.materials[index] == null) this.materials[index] = Material.CLOCK;

            this.titles[index] = resultSet.getString("title");

            // create item
            ItemStack item = new ItemStack(this.materials[index], 1);
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(this.itemPrefix + this.titles[index]);
            item.setItemMeta(itemMeta);

            // add item
            this.times.put(index, resultSet.getInt("time"));
            this.inventory[index] = item;

        } while (resultSet.next());
    }

    /**
     *
     * @param p the player the menu should be shown to
     */
    public void showMenu(Player p) {
        // create the inventory later shown to the player
        Inventory inv = Bukkit.createInventory(p, this.invSize, this.menuTitle);

        // fill inventory with placeholder items
        inv.setContents(this.inventory);

        // show inventory to player
        p.openInventory(inv);
    }

    /**
     *
     * @param title title of new item
     * @param time time that the world should be set to when item is clicked
     * @param material material of new item
     * @param pos position in menu of new item
     * @return error, null if no error
     */
    @Nullable
    public String add(String title, int time, Material material, int pos) {
        try {
            String query = "INSERT INTO `time_gui` (`position`, `time`, `material`, `title`) VALUES (?,?,?,?)";
            PreparedStatement prepStmt = this.connection.prepareStatement(query);
            prepStmt.setString(4, title);
            prepStmt.setInt(2, time);
            prepStmt.setString(3, material.toString());
            prepStmt.setInt(1, pos);
            prepStmt.executeQuery();
        } catch (SQLException e) {
            Main.log(e.getMessage(), Level.SEVERE);
            return "Could not add item. See log for more information";
        }

        this.times.put(pos, time);
        this.materials[pos] = material;
        this.titles[pos] = title;

        ItemStack item = new ItemStack(material, 1);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(this.itemPrefix + title);
        item.setItemMeta(itemMeta);

        this.inventory[pos] = item;

        return null;
    }

    /**
     *
     * @param pos position of item to remove
     * @return error, null if no error
     */
    @Nullable
    public String remove(int pos) {
        try {
            String query = "DELETE FROM `time_gui` WHERE `position` = ?";
            PreparedStatement prepStmt = this.connection.prepareStatement(query);
            prepStmt.setInt(1, pos);
            prepStmt.executeQuery();
        } catch (SQLException e) {
            Main.log(e.getMessage(), Level.SEVERE);
            return "Could not remove item. See log for more information";
        }

        this.times.remove(pos);
        this.materials[pos] = null;
        this.titles[pos] = null;
        this.inventory[pos] = this.placeholder;

        return null;
    }

}
