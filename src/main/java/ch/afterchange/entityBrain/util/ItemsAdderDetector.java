package ch.afterchange.entityBrain.util;

import org.bukkit.Bukkit;

public final class ItemsAdderDetector {

    private ItemsAdderDetector() {}

    public  static boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }
}
