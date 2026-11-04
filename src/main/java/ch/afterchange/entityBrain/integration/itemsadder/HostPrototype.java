package ch.afterchange.entityBrain.integration.itemsadder;

import ch.afterchange.entityBrain.core.BrainRegistryImpl;
import ch.afterchange.entityBrain.species.SpeciesDefinition;
import ch.afterchange.entityBrain.species.SpeciesRegistry;
import ch.afterchange.entityBrain.util.EBLog;
import dev.lone.itemsadder.api.CustomEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class HostPrototype {

    private static BrainRegistryImpl registry;
    private static SpeciesRegistry species;
    private static CustomEntity current;

    private HostPrototype() {}

    public static void init(BrainRegistryImpl brainRegistry) {
        registry = brainRegistry;
    }

    public static String spawn(Player player, String id) {
        if (!ItemsAdderReadyListener.isReady()) return "ItemsAdder not ready yet.";
        if (!CustomEntity.isInRegistry(id)) return "Unknown custom entity: " + id;

        CustomEntity ce = CustomEntity.spawn(id, player.getLocation());
        if (ce == null) return "Spawn failed";
        current = ce;

        SpeciesDefinition sp = species.findByModel(id);
        if (sp == null) {
            EBLog.warn("No species uses model " + id + ": spawned without a brain.");
        } else {
            registry.attachNew(ce.getEntity(), sp);
        }

        Entity base = ce.getEntity();
        EBLog.info("Spawned " + ce.getNamespacedID()
                + " | base type=" + ce.getType()
                + " | base class=" + base.getClass().getSimpleName()
                + " | is Mob=" + (base instanceof Mob)
                + " | animations=" + ce.getAnimationsNames());
        return "Spawned. See console for details.";
    }

    public static String walkToPlayer(Player player) {
        if (current == null) return "No entity spawned.";
        if (!(current.getEntity() instanceof Mob mob)) return "Base entity is not a Mob.";

        boolean ok = mob.getPathfinder().moveTo(player.getLocation());
        return ok ? "Moving." : "No path found.";
    }

    public static String anim(String name) {
        if (current == null) return "No entity spawned.";
        return current.playAnimation(name)
                ? "Playing " + name
                : "Animation not found: " + name;
    }

    public static String info() {
        if (current == null) return "No entity spawned.";
        Entity base = current.getEntity();
        return "valid=" + base.isValid()
                + ", dead=" + base.isDead()
                + ", uuid=" + base.getUniqueId()
                + ", isCustom=" + CustomEntity.isCustomEntity(base)
                + ", byAlreadySpawned=" + (CustomEntity.byAlreadySpawned(base) != null);
    }

    public static String stripVanillaAi() {
        if (current == null) return "No entity spawned.";
        if (!(current.getEntity() instanceof  Mob mob)) return "Base entity is not a Mob.";

        Bukkit.getMobGoals().removeAllGoals(mob);
        mob.setTarget(null);
        return "Vanilla goals removed.";
    }
}
