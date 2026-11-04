package ch.afterchange.entityBrain;

import ch.afterchange.entityBrain.command.BrainsCommand;
import ch.afterchange.entityBrain.command.CommandManager;
import ch.afterchange.entityBrain.command.ReloadCommand;
import ch.afterchange.entityBrain.core.BrainKeys;
import ch.afterchange.entityBrain.core.BrainLifecycleListener;
import ch.afterchange.entityBrain.core.BrainRegistryImpl;
import ch.afterchange.entityBrain.core.BrainScheduler;
import ch.afterchange.entityBrain.integration.itemsadder.HostPrototype;
import ch.afterchange.entityBrain.integration.itemsadder.PrototypeCommand;
import ch.afterchange.entityBrain.util.Banner;
import ch.afterchange.entityBrain.util.EBLog;
import ch.afterchange.entityBrain.util.ItemsAdderDetector;
import ch.afterchange.entityBrain.integration.itemsadder.ItemsAdderReadyListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class EntityBrain extends JavaPlugin {

    private BrainRegistryImpl registry;
    private BrainScheduler scheduler;

    @Override
    public void onEnable() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        saveDefaultConfig();
        EBLog.init(getLogger(), getConfig().getBoolean("debug", false));

        if(getConfig().getBoolean("banner", true)) {
            Banner.print(getPluginMeta().getVersion());
        }

        EBLog.info("EntityBrain enabled.");
        EBLog.info(ItemsAdderDetector.isAvailable()
                ? "ItemsAdder detected."
                : "ItemsAdder not found: animations disabled.");

        BrainKeys.init(this);
        registry = new BrainRegistryImpl();
        scheduler = new BrainScheduler(this, registry);

        getServer().getPluginManager().registerEvents(new BrainLifecycleListener(registry), this);
        scheduler.start();

        int restored = registry.restoreAllLoaded();
        EBLog.info("Brain core ready (" + restored + " brain(s) restored).");

        CommandManager commands = new CommandManager(this);
        commands.add(new ReloadCommand(this));
        commands.add(new BrainsCommand(registry));

        if(ItemsAdderDetector.isAvailable()) {
            getServer().getPluginManager().registerEvents(new ItemsAdderReadyListener(), this);
            HostPrototype.init(registry);
            commands.add(new PrototypeCommand());
        }

        commands.registerAll();
    }

    @Override
    public void onDisable() {
        if (scheduler != null) scheduler.stop();
        if (registry != null) registry.clear();
        EBLog.info("EntityBrain disabled.");
    }
}
