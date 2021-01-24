package de.lulonaut.bot.utils

class Constants {
    var defaultOptions: MutableMap<String?, String?>? = HashMap()

    fun loadDefaultOptions() {
        defaultOptions?.set("counting", "false")
        defaultOptions?.set("guildMemberRole", "false")
        defaultOptions?.set("role", "Hypixel Verified")
        defaultOptions?.set("rankRoles", "false")
        defaultOptions?.set("prefix", "+")
    }
}