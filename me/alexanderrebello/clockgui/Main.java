package me.alexanderrebello.clockgui;

import me.alexanderrebello.clockgui.commands.TimeStoneCommand;
import me.alexanderrebello.clockgui.listeners.MenuClickListener;
import me.alexanderrebello.clockgui.listeners.TimeStoneListener;
import me.alexanderrebello.clockgui.menus.TimeMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public Connection connection = null;
    public TimeMenu timeMenu = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        try {
            log("Connecting to a database...");
            Class.forName("org.mariadb.jdbc.Driver");

            this.connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/minecraft?allowPublicKeyRetrieval=true&useSSL=false", "minecraft", "minecraft");
            log("Connected database successfully...");

            log("Creating table if not exists...");
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("""
                                CREATE TABLE IF NOT EXISTS `time_gui` (
                                    `position` INT(2) NOT NULL DEFAULT '0',
                                    `time` INT(5) NOT NULL DEFAULT '6000',
                                    `material` VARCHAR(32) NOT NULL DEFAULT 'CLOCK',
                                    `title` VARCHAR(32) NOT NULL DEFAULT 'Dawn',
                                    PRIMARY KEY (`position`)
                                );
                                """);
            stmt.close();

            this.createMenu();

            this.getServer().getPluginManager().registerEvents(new TimeStoneListener(this), this);
            this.getServer().getPluginManager().registerEvents(new MenuClickListener(this), this);

            PluginCommand cmd = this.getCommand("timestone");
            assert cmd != null;
            cmd.setExecutor(new TimeStoneCommand(this));
        } catch (Exception e) {
            log(e.getMessage(), Level.SEVERE);
            this.setEnabled(false);
        }
    }

    /**
     * recreate the menu and get the data from the database
     */
    public void createMenu() {
        try {
            Statement stmt = this.connection.createStatement();
            stmt.execute("SELECT * FROM time_gui WHERE `position` BETWEEN 0 AND 53 AND `time` BETWEEN 0 AND 24000 ORDER BY `position` ASC;");

            String menuTitle = this.getConfig().getString("menu-title");
            if (menuTitle == null) menuTitle = "Menu";
            else menuTitle = ChatColor.translateAlternateColorCodes('&', menuTitle);

            String itemPrefix = this.getConfig().getString("menu-item-prefix");
            if (itemPrefix == null) itemPrefix = "Item";
            else itemPrefix = ChatColor.translateAlternateColorCodes('&', itemPrefix);

            boolean addRow = this.getConfig().getBoolean("add-empty-line");
            this.timeMenu = new TimeMenu(this.connection, stmt.getResultSet(), addRow, menuTitle, itemPrefix);
            stmt.close();
        } catch (Exception e) {
            log(e.getMessage(), Level.SEVERE);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (this.connection != null) this.connection.close();
        } catch (SQLException e) {
            log(e.getMessage(), Level.SEVERE);
        }
    }

    public static void log(String msg, Level level) {
        Bukkit.getLogger().log(level, "ClockGUI: " + msg);
    }

    public static void log(String msg) {
        log(msg, Level.INFO);
    }
}

