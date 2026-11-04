package ch.afterchange.entityBrain.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class EBLog {

    private static Logger logger;
    private static boolean debug;

    private EBLog() {}

    public static void init(Logger pluginLogger, boolean debugEnabled) {
        logger = pluginLogger;
        debug = debugEnabled;
    }

    public static void setDebug(boolean enabled) {
        debug = enabled;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warning(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
    public static void error(String message) { logger.severe(message); }

    public static void debug(String message) {
        if (debug) {
            logger.info("[DEBUG] " + message);
        }
    }
}
