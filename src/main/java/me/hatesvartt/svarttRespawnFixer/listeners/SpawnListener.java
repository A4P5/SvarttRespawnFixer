package me.hatesvartt.svarttRespawnFixer.listeners;

import me.hatesvartt.svarttRespawnFixer.SvarttRespawnFixer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Random;

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
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        if (event.isBedSpawn() || event.isAnchorSpawn()) {
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            World world = event.getPlayer().getWorld();
            Location randomSpawn = getRandomSpawn(world);
            event.setRespawnLocation(randomSpawn);
        });
    }

    private Location getRandomSpawn(World world) {
        int centerX = 0; // center X coordinate
        int centerZ = 0; // center Z coordinate
        Location lastLocation = null;
        int attempts = 0;

        while (attempts < maxAttempts) { 
            attempts++;

            double angle = random.nextDouble() * 2 * Math.PI;
            double radius = random.nextDouble() * maxRadius;

            int blockX = (int) (centerX + radius * Math.cos(angle));
            int blockZ = (int) (centerZ + radius * Math.sin(angle));

            int y = world.getHighestBlockYAt(blockX, blockZ);
            Location loc = new Location(world, blockX, y, blockZ);
            lastLocation = loc.clone(); // store last generated location

            if (!loc.getBlock().getType().name().contains("LAVA") &&
                    !loc.clone().subtract(0,1,0).getBlock().getType().name().contains("LAVA")) {
                // safe spot found!
                return loc.add(0, 1, 0);
            }
        }

        // if all attempts fail: fallback to world spawn height
        return lastLocation.add(0, 1, 0);
    }
}
