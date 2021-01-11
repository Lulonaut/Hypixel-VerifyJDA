package de.lulonaut.bot.utils

object Cache {
    var cache: MutableMap<String, Map<String?, String?>?>? = null
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