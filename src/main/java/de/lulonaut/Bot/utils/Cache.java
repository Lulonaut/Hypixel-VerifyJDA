package de.lulonaut.Bot.utils;

import java.util.Map;

public class Cache {

    public static Map<String, Map<String, String>> cache;

    public static void refreshOrAddCache(String GuildID) {
        cache.put(GuildID, Database.loadConfig(GuildID));
    }

    public static Map<String, String> getConfig(String GuildID) {
        if (!cache.containsKey(GuildID)) {
            refreshOrAddCache(GuildID);
        }
        return cache.get(GuildID);
    }
}
