package ch.afterchange.entityBrain.species;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.regex.Pattern;

final class SpeciesLoader {
    record Result(SpeciesDefinition species, List<String> errors, List<String> warnings) {}

    private static final Pattern ID = Pattern.compile("[a-z0-9_]+");

    private static final Set<String> ROOT_KEYS = Set.of(
            "display_name", "entity", "personality", "needs", "behaviors", "animations", "hunting");
    private static final Set<String> ENTITY_KEYS = Set.of("model");
    private static final Set<String> PERSONALITY_KEYS = Set.of("aggression", "courage", "curiosity");
    private static final Set<String> NEEDS_KEYS = Set.of("hunger", "energy", "fear");
    private static final Set<String> HUNTING_KEYS = Set.of("prey", "enemies");

    private static final Set<String> BEHAVIORS = Set.of(
            "idle", "explore", "follow", "hunt", "eat", "sleep", "flee", "socialize",
            "defend", "return_home", "migrate", "seek_shelter", "carry");
    private static final Set<String> ANIMATION_INTENTS = Set.of(
            "idle", "walk", "run", "attack", "eat", "sleep", "scared", "surprised", "death");

    Result load(String id, YamlConfiguration yaml) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (!ID.matcher(id).matches()) {
            errors.add("file name: species id must match [a-z0-9_]+ (got '" + id + "')");
        }

        warnUnknown(yaml, ROOT_KEYS, "", warnings);
        warnUnknown(yaml.getConfigurationSection("entity"), ENTITY_KEYS, "entity", warnings);
        warnUnknown(yaml.getConfigurationSection("personality"), PERSONALITY_KEYS, "personality", warnings);
        warnUnknown(yaml.getConfigurationSection("needs"), NEEDS_KEYS, "needs", warnings);
        warnUnknown(yaml.getConfigurationSection("hunting"), HUNTING_KEYS, "hunting", warnings);

        String displayName = yaml.getString("display_name", id);

        String model = yaml.getString("entity.model");
        if (model == null || model.isBlank()) {
            errors.add("entity.model: required (ItemsAdder entity id, e.g. namespace:name)");
        } else if (!model.contains(":")) {
            errors.add("entity.model: expected 'namespace:name', got '" + model + "'");
        }

        SpeciesDefinition.Personality personality = new SpeciesDefinition.Personality(
                readUnit(yaml, "personality.aggression", 0.5, errors),
                readUnit(yaml, "personality.courage", 0.5, errors),
                readUnit(yaml, "personality.curiosity", 0.5, errors));

        SpeciesDefinition.Needs needs = new SpeciesDefinition.Needs(
                readBool(yaml, "needs.hunger", true, errors),
                readBool(yaml, "needs.energy", true, errors),
                readBool(yaml, "needs.fear", true, errors));

        List<String> behaviors = new ArrayList<>();
        for (String raw : yaml.getStringList("behaviors")) {
            String name = raw.toLowerCase(Locale.ROOT);
            if (BEHAVIORS.contains(name)) {
                behaviors.add(name);
            } else {
                errors.add("behaviors: unknown behavior '" + raw + "' (known: "
                        + String.join(", ", new TreeSet<>(BEHAVIORS)) + ")");
            }
        }
        if (behaviors.isEmpty()) {
            warnings.add("behaviors: empty list, the creature will not do anything");
        }

        Map<String, String> animations = new LinkedHashMap<>();
        ConfigurationSection animSection = yaml.getConfigurationSection("animations");
        if (animSection != null) {
            for (String intent : animSection.getKeys(false)) {
                String value = animSection.getString(intent);
                if (!ANIMATION_INTENTS.contains(intent)) {
                    warnings.add("animations." + intent + ": unknown intention (known: "
                            + String.join(", ", new TreeSet<>(ANIMATION_INTENTS)) + ")");
                }
                if (value == null || value.isBlank()) {
                    errors.add("animations." + intent + ": must be an animation name");
                } else {
                    animations.put(intent, value);
                }
            }
        }

        SpeciesDefinition.Hunting hunting = new SpeciesDefinition.Hunting(
                yaml.getStringList("hunting.prey"),
                yaml.getStringList("hunting.enemies"));

        if (!errors.isEmpty()) {
            return new Result(null, errors, warnings);
        }
        return new Result(new SpeciesDefinition(id, displayName, model, personality, needs,
                behaviors, animations, hunting), errors, warnings);
    }

    private static double readUnit(YamlConfiguration yaml, String path, double def, List<String> errors) {
        if (!yaml.contains(path)) return def;
        if (!yaml.isDouble(path) && !yaml.isInt(path)) {
            errors.add(path + ": must be a number between 0.0 and 1.0");
            return def;
        }
        double value = yaml.getDouble(path);
        if (value < 0.0 || value > 1.0) {
            errors.add(path + ": must be between 0.0 and 1.0, got " + value);
            return def;
        }
        return value;
    }

    private static boolean readBool(YamlConfiguration yaml, String path, boolean def, List<String> errors) {
        if (!yaml.contains(path)) return def;
        if (!yaml.isBoolean(path)) {
            errors.add(path + ": must be true or false");
            return def;
        }
        return yaml.getBoolean(path);
    }

    private static void warnUnknown(ConfigurationSection section, Set<String> allowed,
                                    String path, List<String> warnings) {
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            if (!allowed.contains(key)) {
                warnings.add((path.isEmpty() ? key : path + "." + key) + ": unknown key (typo?)");
            }
        }
    }
}
