package ch.afterchange.entityBrain;

import ch.afterchange.entityBrain.command.CommandManager;
import ch.afterchange.entityBrain.command.ReloadCommand;
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

        CommandManager commands = new CommandManager(this);
        commands.add(new ReloadCommand(this));

        if(ItemsAdderDetector.isAvailable()) {
            getServer().getPluginManager().registerEvents(new ItemsAdderReadyListener(), this);
            commands.add(new PrototypeCommand());
        }

        commands.registerAll();
    }

    @Override
    public void onDisable() {
        EBLog.info("EntityBrain disabled.");
    }
}
