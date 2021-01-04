package de.lulonaut.Bot.utils;

import redis.clients.jedis.Jedis;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Database {
    static Jedis j = new Jedis("localhost");

    public static void addMessage(String GuildID, String UserID) {
        //on message
        j.hincrBy("messages:" + GuildID, UserID, 1);
    }

    public static void removeMessage(String GuildID, String UserID) {
        //on message deleted
        j.hincrBy("messages:" + GuildID, UserID, -1);
    }

    public static long lookupUser(String GuildID, String UserID) {
        //if one user wants to know their messages
        String lookup = j.hget("messages:" + GuildID, UserID);
        if (lookup == null) {
            return 0L;
        } else {
            return Long.parseLong(lookup);
        }
    }

    public static Map<String, Integer> sort(String GuildID) {
        Map<String, String> messages = j.hgetAll("messages:" + GuildID);
        Map<String, Integer> longMessages = new HashMap<>();

        for (String key : messages.keySet()) {
            longMessages.put(key, Integer.parseInt(messages.get(key)));
        }

        LinkedHashMap<String, Integer> sortedMessages = new LinkedHashMap<>();
        longMessages.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMessages.put(x.getKey(), x.getValue()));
        return sortedMessages;
    }

    public static void removeAllMessages(String GuildID) {
        //ask for confirmation before doing this
        j.del("messages:" + GuildID);
    }
}
