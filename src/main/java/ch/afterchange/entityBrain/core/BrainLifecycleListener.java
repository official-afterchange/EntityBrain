package ch.afterchange.entityBrain.core;

import ch.afterchange.entityBrain.util.EBLog;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;

public final class BrainLifecycleListener implements Listener {

    private final BrainRegistryImpl registry;

    public BrainLifecycleListener(BrainRegistryImpl registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onAdd(EntityAddToWorldEvent event) {
        registry.restore(event.getEntity());
    }

    @EventHandler
    public void onRemove(EntityRemoveEvent event) {
        Entity entity = event.getEntity();
        EntityBrain brain = registry.deactivate(entity.getUniqueId());
        if (brain == null) return;

        switch (event.getCause()) {
            case DEATH -> EBLog.debug("Brain " + brain.brainId() + " ended: entity died.");
            case UNLOAD -> EBLog.debug("Brain " + brain.brainId() + " unloaded.");
            default -> EBLog.warn("Brain " + brain.brainId() + " lost unexpectedly (" + event.getCause() + ").");
        }
    }
}
