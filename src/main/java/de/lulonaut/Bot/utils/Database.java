package de.lulonaut.Bot.utils;

import redis.clients.jedis.Jedis;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Database {
    static Jedis j = new Jedis("localhost");

    /**
     * Adds a message to the database
     *
     * @param GuildID ID of the server (called guild internally)
     * @param UserID  ID of the user
     */
    public static void addMessage(String GuildID, String UserID) {
        j.hincrBy("messages:" + GuildID, UserID, 1);
    }

    /**
     * removes a message from the database
     *
     * @param GuildID ID of the server (called guild internally)
     * @param UserID  ID of the user
     */
    public static void removeMessage(String GuildID, String UserID) {
        //on message deleted
        //TODO: find a way to get UserID
        j.hincrBy("messages:" + GuildID, UserID, -1);
    }

    /**
     * Returns the number of messages the user has in a specific server
     *
     * @param GuildID ID of the server (called guild internally)
     * @param UserID  ID of the user
     * @return number of messages
     */
    public static long lookupUser(String GuildID, String UserID) {
        //if one user wants to know their messages
        String lookup = j.hget("messages:" + GuildID, UserID);
        if (lookup == null) {
            return 0L;
        } else {
            return Long.parseLong(lookup);
        }
    }

    /**
     * Returns a sorted map with the users and amount of messages
     *
     * @param GuildID ID of the server (called guild internally)
     * @return sorted map of users and messages
     */
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

    /**
     * Removes all messages for a specific server
     *
     * @param GuildID ID of the server (called guild internally)
     */
    public static void removeAllMessages(String GuildID) {
        //ask for confirmation before doing this
        j.del("messages:" + GuildID);
    }
}
