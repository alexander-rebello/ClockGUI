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

public class Main extends JavaPlugin {

    private Connection connection = null;
    public TimeMenu timeMenu = null;

    @Override
    public void onEnable() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/test", "minecraft", "minecraft");

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

            stmt = connection.createStatement();
            if (!stmt.execute("SELECT * FROM time_gui WHERE `position` BETWEEN 0 AND 44 AND `time` BETWEEN 0 AND 24000 SORT BY `position` ASC;")) throw new Exception("");
            this.timeMenu = new TimeMenu(stmt.getResultSet(), true, ChatColor.BLUE + "Clock GUI", ChatColor.WHITE + "Click for " + ChatColor.GOLD);
            stmt.close();

            getServer().getPluginManager().registerEvents(new TimeStoneListener(this), this);
            getServer().getPluginManager().registerEvents(new MenuListener(this), this);

            getCommand("timestone").setExecutor(new TimeStoneCommand(this));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
        }
    }
}

