package de.lulonaut.bot.utils

object Cache {
    private var cache: MutableMap<String, Map<String?, String?>?>? = HashMap()
    fun refreshOrAddCache(GuildID: String) {
        println("database request!")
        cache!![GuildID] = Database.loadConfig(GuildID)
    }

    fun getConfig(GuildID: String): Map<String?, String?>? {
        if (!cache!!.containsKey(GuildID)) {
            refreshOrAddCache(GuildID)
        }
        return cache!![GuildID]
    }
}