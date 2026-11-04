package ch.afterchange.entityBrain.species;

import ch.afterchange.entityBrain.util.EBLog; // adapte au package de ton EBLog
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class SpeciesRegistry {

    public record ReloadResult(int loaded, int failed, int kept) {}

    private final JavaPlugin plugin;
    private final SpeciesLoader loader = new SpeciesLoader();
    private volatile Map<String, SpeciesDefinition> species = Map.of();

    public SpeciesRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ReloadResult reload() {
        File folder = new File(plugin.getDataFolder(), "species");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) files = new File[0];
        Arrays.sort(files);

        Map<String, SpeciesDefinition> previous = species;
        Map<String, SpeciesDefinition> next = new LinkedHashMap<>();
        int loaded = 0, failed = 0, kept = 0;

        for (File file : files) {
            String fileName = file.getName();
            String id = fileName.substring(0, fileName.length() - ".yml".length());

            YamlConfiguration yaml = new YamlConfiguration();
            try {
                yaml.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                EBLog.error("[" + fileName + "] cannot be read: " + e.getMessage(), e);
                failed++;
                if (keepPrevious(id, previous, next)) kept++;
                continue;
            }

            SpeciesLoader.Result result = loader.load(id, yaml);
            result.warnings().forEach(w -> EBLog.warn("[" + fileName + "] " + w));

            if (result.species() == null) {
                result.errors().forEach(err -> EBLog.error("[" + fileName + "] " + err));
                failed++;
                if (keepPrevious(id, previous, next)) kept++;
                continue;
            }
            next.put(id, result.species());
            loaded++;
        }

        for (SpeciesDefinition def : next.values()) {
            checkReferences(def, "hunting.prey", def.hunting().prey(), next);
            checkReferences(def, "hunting.enemies", def.hunting().enemies(), next);
        }

        species = Collections.unmodifiableMap(next);
        EBLog.info("Species: " + loaded + " loaded, " + failed + " failed.");
        return new ReloadResult(loaded, failed, kept);
    }

    public SpeciesDefinition get(String id) {
        return species.get(id);
    }

    public Collection<SpeciesDefinition> all() {
        return species.values();
    }

    public Set<String> ids() {
        return species.keySet();
    }

    public SpeciesDefinition findByModel(String model) {
        for (SpeciesDefinition def : species.values()) {
            if (def.model().equals(model)) return def;
        }
        return null;
    }

    private static boolean keepPrevious(String id, Map<String, SpeciesDefinition> previous,
                                        Map<String, SpeciesDefinition> next) {
        SpeciesDefinition old = previous.get(id);
        if (old == null) return false;
        next.put(id, old);
        EBLog.warn("[" + id + ".yml] keeping the previous valid version.");
        return true;
    }

    private static void checkReferences(SpeciesDefinition def, String path, List<String> refs,
                                        Map<String, SpeciesDefinition> all) {
        for (String ref : refs) {
            if (!all.containsKey(ref)) {
                EBLog.warn("[" + def.id() + ".yml] " + path + ": unknown species '" + ref + "'");
            }
        }
    }
}