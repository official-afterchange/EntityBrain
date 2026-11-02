package ch.afterchange.entityBrain;

import ch.afterchange.entityBrain.util.EBLog;
import ch.afterchange.entityBrain.util.ItemsAdderDetector;
import ch.afterchange.entityBrain.integration.itemsadder.ItemsAdderReadyListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class EntityBrain extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        EBLog.init(getLogger(), getConfig().getBoolean("debug", false));

        EBLog.info("EntityBrain enabled.");
        EBLog.info(ItemsAdderDetector.isAvailable()
                ? "ItemsAdder detected."
                : "ItemsAdder not found: animations disabled.");

        if(ItemsAdderDetector.isAvailable()) {
            getServer().getPluginManager().registerEvents(new ItemsAdderReadyListener(), this);
        }
    }

    @Override
    public void onDisable() {
        EBLog.info("EntityBrain disabled.");
    }
}
