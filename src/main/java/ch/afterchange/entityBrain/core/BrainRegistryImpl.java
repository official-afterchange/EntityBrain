package ch.afterchange.entityBrain.core;

import ch.afterchange.entityBrain.api.Brain;
import ch.afterchange.entityBrain.api.BrainRegistry;
import ch.afterchange.entityBrain.species.SpeciesDefinition;
import ch.afterchange.entityBrain.species.SpeciesRegistry;
import ch.afterchange.entityBrain.util.EBLog;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public final class BrainRegistryImpl implements BrainRegistry {

    private final Map<UUID, EntityBrain> byEntity = new HashMap<>();
    private int nextSlot;
    private final SpeciesRegistry speciesRegistry;

    public BrainRegistryImpl(SpeciesRegistry speciesRegistry) {
        this.speciesRegistry = speciesRegistry;
    }

    public EntityBrain attachNew(Entity entity, SpeciesDefinition species) {
        UUID brainId = UUID.randomUUID();
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(BrainKeys.SPECIES, PersistentDataType.STRING, species.id());
        pdc.set(BrainKeys.BRAIN_ID, PersistentDataType.STRING, brainId.toString());
        return activate(entity, species, brainId);
    }

    public EntityBrain restore(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String speciesId = pdc.get(BrainKeys.SPECIES, PersistentDataType.STRING);
        String rawId = pdc.get(BrainKeys.BRAIN_ID, PersistentDataType.STRING);
        if (speciesId == null || rawId == null) return null;

        EntityBrain existing = byEntity.get(entity.getUniqueId());
        if (existing != null) return existing;

        SpeciesDefinition species = speciesRegistry.get(speciesId);
        if (species == null) {
            EBLog.warn("Entity " + entity.getUniqueId() + " uses unknown species '"
                + speciesId + "': no brain activated.");
            return null;
        }

        UUID brainId;
        try {
            brainId = UUID.fromString(rawId);
        } catch (IllegalArgumentException e) {
            EBLog.warn("Invalid brain id on entity " + entity.getUniqueId());
            return null;
        }
        return activate(entity, species, brainId);
    }

    public int restoreAllLoaded() {
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (restore(entity) != null) count++;
            }
        }
        return count;
    }

    public EntityBrain deactivate(UUID entityId) {
        return byEntity.remove(entityId);
    }

    public void clear() {
        byEntity.clear();
    }

    public List<EntityBrain> snapshot() {
        return new ArrayList<>(byEntity.values());
    }

    private void keepAlive(Entity entity) {
        entity.setPersistent(true);
        if (entity instanceof LivingEntity living) {
            living.setRemoveWhenFarAway(false);
        }
    }

    private EntityBrain activate(Entity entity, SpeciesDefinition species, UUID brainId) {
        keepAlive(entity);
        int slot = nextSlot++ % BrainScheduler.TICK_INTERVAL;
        EntityBrain brain = new EntityBrain(brainId,
                new BrainContext(entity, species, speciesRegistry), slot);
        byEntity.put(entity.getUniqueId(), brain);
        EBLog.debug("Brain " + brainId + " active for entity " + entity.getUniqueId()
            + " (" + species.id() + ", slot " + slot + ")");
        return brain;
    }

    @Override public Optional<Brain> get(Entity entity) { return get(entity.getUniqueId()); }
    @Override public Optional<Brain> get(UUID entityId) { return Optional.<Brain>ofNullable(byEntity.get(entityId)); }
    @Override public Collection<? extends Brain> all() { return Collections.unmodifiableCollection(byEntity.values()); }
    @Override public int size() { return byEntity.size(); }
}
