package de.lulonaut.bot.cache

import de.lulonaut.bot.utils.Database

object DatabaseCache {
    private var cache: MutableMap<String, Map<String?, String?>?>? = HashMap()
    fun refreshOrAddCache(GuildID: String) {
        cache!![GuildID] = Database.loadConfig(GuildID)
    }

    fun getConfig(GuildID: String): Map<String?, String?>? {
        if (!cache!!.containsKey(GuildID)) {
            refreshOrAddCache(GuildID)
        }
        return cache!![GuildID]
    }
}