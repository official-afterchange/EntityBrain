package ch.afterchange.entityBrain.core;

import ch.afterchange.entityBrain.util.EBLog;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class BrainScheduler {

    public static final int TICK_INTERVAL = 5;

    private final JavaPlugin plugin;
    private final BrainRegistryImpl registry;
    private BukkitTask task;
    private long tick;

    public BrainScheduler(JavaPlugin plugin, BrainRegistryImpl registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::run, 1L, 1L);
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    public void run() {
        tick++;
        int slot = (int) (tick % TICK_INTERVAL);
        for (EntityBrain brain : registry.snapshot()) {
            if (brain.slot() != slot) continue;
            if (!brain.context().hostValid()) continue;
            try {
                brain.tick(tick);
            } catch (Throwable t) {
                EBLog.error("Brain tick failed: " + brain.brainId(), t);
            }
        }
    }
}
