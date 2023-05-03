package me.alexanderrebello.clockgui;

import me.alexanderrebello.clockgui.commands.TimeStoneCommand;
import me.alexanderrebello.clockgui.listeners.MenuListener;
import me.alexanderrebello.clockgui.listeners.TimeStoneListener;
import me.alexanderrebello.clockgui.menus.TimeMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Main extends JavaPlugin {

    private Connection connection = null;
    public TimeMenu timeMenu = null;

    @Override
    public void onEnable() {
        try {
            this.log("Connecting to a database...");
            Class.forName("org.mariadb.jdbc.Driver");

            this.connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/minecraft?allowPublicKeyRetrieval=true&useSSL=false", "minecraft", "minecraft");
            this.log("Connected database successfully...");

            this.log("Creating table if not exists...");
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

            getServer().getPluginManager().registerEvents(new TimeStoneListener(this), this);
            getServer().getPluginManager().registerEvents(new MenuListener(this), this);

            getCommand("timestone").setExecutor(new TimeStoneCommand(this));
        } catch (Exception e) {
            this.log(e.getMessage(), Level.SEVERE);
            this.setEnabled(false);
        }
    }

    public void createMenu() {
        try {
            Statement stmt = this.connection.createStatement();
            stmt.execute("SELECT * FROM time_gui WHERE `position` < 54 AND `time` < 24000 ORDER BY `position` ASC;");
            this.timeMenu = new TimeMenu(this.connection, stmt.getResultSet(), true, ChatColor.BLUE + "Clock GUI", ChatColor.WHITE + "Click for " + ChatColor.GOLD);
            stmt.close();
        } catch (Exception e) {
            this.log(e.getMessage(), Level.SEVERE);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (this.connection != null) this.connection.close();
        } catch (SQLException e) {
            this.log(e.getMessage(), Level.SEVERE);
        }
    }

    public static void log(String msg, Level level) {
        Bukkit.getLogger().log(level, "ClockGUI: " + msg);
    }

    public static void log(String msg) {
        log(msg, Level.INFO);
    }
}

