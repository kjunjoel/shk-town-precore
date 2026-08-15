package kr.shkworld.shktown.ui.apps.navigation;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.navigation.model.NavigationConfig;
import kr.shkworld.shktown.core.navigation.model.NavigationDirection;
import kr.shkworld.shktown.util.LocationUtil;
import kr.shkworld.shktown.util.MessageUtil;
import kr.toxicity.hud.api.BetterHudAPI;
import kr.toxicity.hud.api.adapter.LocationWrapper;
import kr.toxicity.hud.api.adapter.WorldWrapper;
import kr.toxicity.hud.api.player.HudPlayer;
import kr.toxicity.hud.api.player.PointedLocation;
import kr.toxicity.hud.api.player.PointedLocationSource;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NavigationManager {
    private final SHKTown plugin;

    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();
    private final Map<UUID, NavigationSession> activeSessions = new HashMap<>();

    public NavigationManager(SHKTown plugin) {
        this.plugin = plugin;
    }

    public void startNavigation(Player player, String destinationName, Location destination) {
        UUID uuid = player.getUniqueId();
        NavigationConfig config = plugin.getNavigationService().getConfig();

        stopNavigationInternal(player);

        if (!player.getWorld().equals(destination.getWorld())) {
            MessageUtil.send(player, config.differentWorld(), config.useGlobalPrefix());
            return;
        }

        activeSessions.put(uuid, new NavigationSession(destinationName, destination));

        try {
            HudPlayer hudPlayer = BetterHudAPI.inst().getPlayerManager().getHudPlayer(uuid);
            if (hudPlayer != null) {
                PointedLocation pointedLocation = getPointedLocation(destination, config);

                hudPlayer.pointers().add(pointedLocation);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("BetterHUD Pointer registration failed: " + e.getMessage());
        }

        String startMessage = config.started().replace("{destination}", destinationName);
        MessageUtil.send(player, startMessage, config.useGlobalPrefix());

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    stopNavigation(player, false);
                    return;
                }

                Location currentLocation = player.getLocation();
                if (!currentLocation.getWorld().equals(destination.getWorld())) {
                    stopNavigation(player, false);
                    return;
                }

                double distance = currentLocation.distance(destination);

                if (distance <= config.arrivalRadius()) {
                    stopNavigation(player, true);
                    return;
                }

                Position fromPos = LocationUtil.toPosition(currentLocation);
                Position toPos = LocationUtil.toPosition(destination);
                NavigationDirection dir = NavigationDirection.calculate(fromPos, toPos, 10.0);

                int distanceInt = (int) Math.round(distance);
                String formattedActionBar = String.format("&b%s &f| %dm &f| &a%s", destinationName, distanceInt, dir.getText());
                player.sendActionBar(MessageUtil.parse(formattedActionBar));

                spawnDestinationParticles(player, destination);
            }
        }.runTaskTimer(plugin, 0L, config.updateIntervalTicks());

        activeTasks.put(uuid, task);
    }

    private static PointedLocation getPointedLocation(Location destination, NavigationConfig config) {
        LocationWrapper locationWrapper = new LocationWrapper(
                new WorldWrapper(destination.getWorld().getName()),
                destination.getX(),
                destination.getY(),
                destination.getZ(),
                0.0f, 0.0f
        );

        return new PointedLocation(
                PointedLocationSource.INTERNAL,
                config.betterhudPointerId(),
                config.betterhudPointerId(),
                locationWrapper
        );
    }

    public void stopNavigation(Player player, boolean isArrived) {
        UUID uuid = player.getUniqueId();

        NavigationSession session = activeSessions.get(uuid);
        boolean wasNavigation = stopNavigationInternal(player);

        if (!wasNavigation || session == null) {
            return;
        }

        NavigationConfig config = plugin.getNavigationService().getConfig();

        if (isArrived) {
            String arrivedMessage = config.arrived().replace("{destination}", session.name());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            MessageUtil.send(player, arrivedMessage, config.useGlobalPrefix());
            MessageUtil.sendTitle(player, "§a✔ 목적지 도착!", "§f" + session.name() + "에 도착했습니다.");
        } else {
            MessageUtil.send(player, config.stopped(), config.useGlobalPrefix());
        }
    }

    private boolean stopNavigationInternal(Player player) {
        UUID uuid = player.getUniqueId();
        NavigationConfig config = plugin.getNavigationService().getConfig();

        BukkitTask task = activeTasks.remove(uuid);
        boolean hadTask = (task != null);

        if (hadTask) {
            task.cancel();
        }

        activeSessions.remove(uuid);

        try {
            HudPlayer hudPlayer = BetterHudAPI.inst().getPlayerManager().getHudPlayer(uuid);
            if (hudPlayer != null) {
                hudPlayer.pointers().removeIf(pt -> config.betterhudPointerId().equalsIgnoreCase(pt.name()));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("BetterHUD Pointer removal failed for " + player.getName() + ": " + e.getMessage());
        }

        if (player.isOnline()) {
            player.sendActionBar(MessageUtil.parse(""));
        }

        return hadTask;
    }

    private void spawnDestinationParticles(Player player, Location destination) {
        if (destination.getWorld() == null) return;

        for (double yOffset = 0; yOffset <= 10; yOffset += 0.5) {
            Location particleLocation = destination.clone().add(0, yOffset, 0);

            player.spawnParticle(
                    Particle.FIREWORK,
                    particleLocation,
                    1,
                    0.1, 0.1, 0.1,
                    0.01
            );

            if (yOffset == 0) {
                player.spawnParticle(
                        Particle.HAPPY_VILLAGER,
                        particleLocation,
                        5,
                        0.3, 0.1, 0.3,
                        0.0
                );
            }
        }
    }

    public boolean isNavigating(Player player) {
        return activeTasks.containsKey(player.getUniqueId());
    }

    public String getDestinationName(Player player) {
        if (player == null) return null;
        NavigationSession session = activeSessions.get(player.getUniqueId());
        return session != null ? session.name() : null;
    }

    public void openScreen(Player player) {
        NavigationScreen navigationScreen = new NavigationScreen(plugin, player);
        player.openInventory(navigationScreen.getInventory());
    }

    private record NavigationSession(String name, Location location) {}
}