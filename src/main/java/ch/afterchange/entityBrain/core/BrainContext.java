package ch.afterchange.entityBrain.core;

import org.bukkit.entity.Entity;

public final class BrainContext {

    private final Entity host;
    private final String speciesId;

    public BrainContext(Entity host, String speciesId) {
        this.host = host;
        this.speciesId = speciesId;
    }

    public Entity host() { return host; }
    public String speciesId() { return speciesId; }
    public boolean hostValid() { return host.isValid(); }
}
