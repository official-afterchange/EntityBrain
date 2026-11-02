package ch.afterchange.entityBrain.integration.itemsadder;

import ch.afterchange.entityBrain.util.EBLog;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ItemsAdderReadyListener implements Listener {

    private static volatile boolean ready;

    public static boolean isReady() {
        return ready;
    }

    @EventHandler
    public void onLoad(ItemsAdderLoadDataEvent event) {
        ready = true;
        EBLog.info("ItemsAddder data loaded.");
    }
}
