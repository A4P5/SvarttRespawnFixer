package me.hatesvartt.svarttRespawnFixer;

import me.hatesvartt.svarttRespawnFixer.listeners.SpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SvarttRespawnFixer extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig(); 
      
        Bukkit.getPluginManager().registerEvents(new SpawnListener(this), this);
        getLogger().info("SvarttRespawnFixer enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SvarttRespawnFixer disabled!");
    }
}
