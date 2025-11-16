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
        getLogger().info("SvarttRespawnFixer has been produced by hatesvratt and Ainomia.\nIt has been designed for use on the AINOMIA server.\nSupport for implementation on your own server will not be provided.\nRespawn logic produced by Ainomia\nPlugin base created by hatesvratt");
    }

    @Override
    public void onDisable() {
        getLogger().info("SvarttRespawnFixer disabled!");
    }
}
