package ch.afterchange.entityBrain;

import ch.afterchange.entityBrain.util.EBLog;
import ch.afterchange.entityBrain.util.ItemsAdderDetector;
import dev.lone.itemsadder.api.ItemsAdder;
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
    }

    @Override
    public void onDisable() {
        EBLog.info("EntityBrain disabled.");
    }
}
