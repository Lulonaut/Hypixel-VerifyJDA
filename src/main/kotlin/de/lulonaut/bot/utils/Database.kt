package de.lulonaut.bot.utils

import redis.clients.jedis.Jedis
import java.util.*

object Database {
    private var j = Jedis("localhost")

    /**
     * Adds a message to the database
     *
     * @param GuildID ID of the server (called guild internally)
     * @param UserID  ID of the user
     */
    fun addMessage(GuildID: String, UserID: String?) {
        j.hincrBy("verifyBot:messages:$GuildID", UserID, 1)
    }

    /**
     * removes a message from the database
     *
     * @param GuildID ID of the server (called guild internally)
     * @param UserID  ID of the user
     */
    fun removeMessage(GuildID: String, UserID: String) {
        j.hincrBy("verifyBot:messages:$GuildID", UserID, -1)
    }

    /**
     * Returns the number of messages the user has in a specific server
     *
     * @param GuildID ID of the server (called guild internally)
     * @param UserID  ID of the user
     * @return number of messages
     */
    fun lookupUser(GuildID: String, UserID: String?): Long {
        //if one user wants to know their messages
        val lookup = j.hget("verifyBot:messages:$GuildID", UserID)
        return lookup?.toLong() ?: 0L
    }

    /**
     * Returns a sorted map with the users and amount of messages
     *
     * @param GuildID ID of the server (called guild internally)
     * @return sorted map of users and messages
     */
    fun sort(GuildID: String): Map<String, Int> {
        val messages = j.hgetAll("verifyBot:messages:$GuildID")
        val longMessages: MutableMap<String, Int> = HashMap()
        for (key in messages.keys) {
            longMessages[key] = messages[key]!!.toInt()
        }
        val sortedMessages = LinkedHashMap<String, Int>()
        longMessages.entries
            .stream()
            .sorted(java.util.Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered { x: Map.Entry<String, Int> -> sortedMessages[x.key] = x.value }
        return sortedMessages
    }

    /**
     * Removes all messages for a specific server
     *
     * @param GuildID ID of the server (called guild internally)
     */
    fun removeAllMessages(GuildID: String) {
        //ask for confirmation before doing this
        j.del("verifyBot:messages:$GuildID")
    }

    fun saveConfig(GuildID: String, options: MutableMap<String?, String?>?) {
        j.hset("verifyBot:config:$GuildID", options)
    }

    fun loadConfig(GuildID: String): Map<String?, String?> {
        return j.hgetAll("verifyBot:config:$GuildID")
    }
}