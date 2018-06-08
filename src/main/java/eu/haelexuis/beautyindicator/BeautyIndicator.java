package eu.haelexuis.beautyindicator;

import eu.haelexuis.beautyindicator.command.ReloadCommand;
import eu.haelexuis.beautyindicator.controller.CombatController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class BeautyIndicator extends JavaPlugin {
    private CombatController combatController;

    @Override
    public void onEnable() {
        reload();

        this.getCommand("beautyindicator").setExecutor(new ReloadCommand(this));

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &aPlugin successfully enabled!"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &7Spigot page: &aidk"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &7Author: &ehaelexuis &a[https://haelexuis.eu]"));
    }

    public void reload() {
        if(combatController != null)
            combatController.onDisable();

        saveDefaultConfig();

        combatController = new CombatController(this, getConfig());

        Bukkit.getPluginManager().registerEvents(combatController, this);
    }

    @Override
    public void onDisable() {
        combatController.onDisable();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &6Plugin successfully disabled!"));
    }
}
