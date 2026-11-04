package ch.afterchange.entityBrain.core;

import ch.afterchange.entityBrain.species.SpeciesDefinition;
import ch.afterchange.entityBrain.species.SpeciesRegistry;
import org.bukkit.entity.Entity;

public final class BrainContext {

    private final Entity host;
    private final String speciesId;
    private final SpeciesRegistry speciesRegistry;
    private SpeciesDefinition lastknown;

    public BrainContext(Entity host, SpeciesDefinition species, SpeciesRegistry speciesRegistry) {
        this.host = host;
        this.speciesId = species.id();
        this.speciesRegistry = speciesRegistry;
        this.lastknown = species;
    }

    public Entity host() { return host; }
    public String speciesId() { return speciesId; }
    public boolean hostValid() { return host.isValid(); }

    public SpeciesDefinition species() {
        SpeciesDefinition current = speciesRegistry.get(speciesId);
        if (current != null) lastknown = current;
        return lastknown;
    }
}
