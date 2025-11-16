package me.hatesvartt.svarttRespawnFixer.listeners;

import me.hatesvartt.svarttRespawnFixer.SvarttRespawnFixer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SpawnListener implements Listener {

    private final SvarttRespawnFixer plugin;
    private final int maxRadius;
    private final int maxAttempts;
    private final Random random = new Random();

    public SpawnListener(SvarttRespawnFixer plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        this.maxRadius = config.getInt("max-radius", 100);
        this.maxAttempts = config.getInt("max-attempts", 15);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, schedulerTask -> {
            if (!player.isOnline()) {
                schedulerTask.cancel();
                return;
            }

            if (!player.isDead()) {
                player.getScheduler().runAtFixedRate(plugin, entityTask -> {
                    Location bedSpawn = null;
                    try {
                        bedSpawn = player.getBedSpawnLocation();
                    } catch (Exception e) {
                        // if it's not accessible, bedSpawn stays null
                    }

                    if (bedSpawn != null) {
                        player.teleportAsync(bedSpawn);
                    } else {
                        spawnRandomSafe(player.getWorld(), loc -> player.teleportAsync(loc));
                    }

                    entityTask.cancel();
                }, null, 1L, 1L);

                schedulerTask.cancel();
            }
        }, 1L, 1L);
    }


    private void spawnRandomSafe(World world, Consumer<Location> callback) {
        attemptSpawn(world, 0, callback, null);
    }

    private void attemptSpawn(World world, int attempt, Consumer<Location> callback, Location lastLocation) {
        if (attempt >= maxAttempts) {
            if (lastLocation != null) {
                Location fallback = lastLocation.clone().add(0, 1, 0);
                plugin.getServer().getRegionScheduler().run(plugin, world, fallback.getChunk().getX(), fallback.getChunk().getZ(), task -> {
                    callback.accept(fallback);
                });
            }
            return;
        }

        double angle = random.nextDouble() * 2 * Math.PI;
        double radius = random.nextDouble() * maxRadius;
        int x = (int) (radius * Math.cos(angle));
        int z = (int) (radius * Math.sin(angle));

        plugin.getServer().getRegionScheduler().run(plugin, world, x >> 4, z >> 4, task -> {
            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x, y, z);

            if (!loc.getBlock().getType().name().contains("LAVA") &&
                    !loc.clone().subtract(0, 1, 0).getBlock().getType().name().contains("LAVA")) {
                callback.accept(loc.add(0, 1, 0));
            } else {
                attemptSpawn(world, attempt + 1, callback, loc);
            }
        });
    }
}
