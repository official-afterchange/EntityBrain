package ch.afterchange.entityBrain.core;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class BrainKeys {

    public static NamespacedKey SPECIES;
    public static NamespacedKey BRAIN_ID;

    private BrainKeys() {}

    public static void init(JavaPlugin plugin) {
        SPECIES = new NamespacedKey(plugin, "species");
        BRAIN_ID = new NamespacedKey(plugin, "brain_id");
    }
}
