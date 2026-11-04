package ch.afterchange.entityBrain.species;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record SpeciesDefinition(
        String id,
        String displayName,
        String model,
        Personality personality,
        Needs needs,
        List<String> behaviors,
        Map<String, String> animations,
        Hunting hunting) {

    public record Personality(double aggression, double courage, double curiosity) {}

    public record Needs(boolean hunger, boolean energy, boolean fear) {}

    public record Hunting(List<String> prey, List<String> enemies) {
        public Hunting {
            prey = List.copyOf(prey);
            enemies = List.copyOf(enemies);
        }
    }

    public SpeciesDefinition {
        behaviors = List.copyOf(behaviors);
        animations = Collections.unmodifiableMap(new LinkedHashMap<>(animations));
    }
}