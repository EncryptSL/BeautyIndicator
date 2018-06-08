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
        combatController = new CombatController(this, getConfig());

        this.getCommand("beautyindicator").setExecutor(new ReloadCommand(this));

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &aPlugin successfully enabled!"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &7Spigot page: &ahttps://www.spigotmc.org/resources/.57546/"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &7Author: &ehaelexuis &a[https://haelexuis.eu]"));

        Bukkit.getPluginManager().registerEvents(combatController, this);
    }

    public void onReload() {
        saveDefaultConfig();
        reloadConfig();
        combatController.onReload(getConfig());
    }

    @Override
    public void onDisable() {
        combatController.onDisable();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &6Plugin successfully disabled!"));
    }
}
