package me.alexanderrebello.clockgui;

import me.alexanderrebello.clockgui.commands.TimeStoneCommand;
import me.alexanderrebello.clockgui.listeners.MenuListener;
import me.alexanderrebello.clockgui.listeners.TimeStoneListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new TimeStoneListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getCommand("timestone").setExecutor(new TimeStoneCommand());
    }

    @Override
    public void onDisable() {

    }
}

