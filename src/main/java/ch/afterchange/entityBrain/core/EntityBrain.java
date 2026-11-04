package ch.afterchange.entityBrain.core;

import ch.afterchange.entityBrain.api.Brain;

import java.util.UUID;

public final class EntityBrain implements Brain {

    private final UUID brainId;
    private final BrainContext context;
    private final int slot;
    private long tickCount;

    EntityBrain(UUID brainId, BrainContext context, int slot) {
        this.brainId = brainId;
        this.context = context;
        this.slot = slot;
    }

    @Override public UUID brainId() { return brainId; }
    @Override public UUID entityId() { return context.host().getUniqueId(); }
    @Override public String speciesId() { return context.speciesId(); }

    public BrainContext context() { return context; }
    public int slot() { return slot; }
    public long tickCount() { return tickCount; }

    void tick(long serverTick) {
        tickCount++;
    }
}
