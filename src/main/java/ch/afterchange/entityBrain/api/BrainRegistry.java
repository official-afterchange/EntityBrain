package ch.afterchange.entityBrain.api;

import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface BrainRegistry {
    Optional<Brain> get(Entity entity);
    Optional<Brain> get(UUID entityId);
    Collection<? extends Brain> all();
    int size();
}
