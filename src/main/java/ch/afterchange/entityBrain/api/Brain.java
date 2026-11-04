package ch.afterchange.entityBrain.api;

import java.util.UUID;

public interface Brain {
    UUID brainId();
    UUID entityId();
    String speciesId();
}
