package net.new_liberty.nlcore.database;

/**
 * EasyDB API.
 */
public class EasyDB {
    private static EasyDBPlugin instance;

    static void setInstance(EasyDBPlugin plugin) {
        instance = plugin;
    }

    /**
     * Gets the EasyDB database instance.
     *
     * @return
     */
    public static Database getDb() {
        return instance.getDb();
    }
}
