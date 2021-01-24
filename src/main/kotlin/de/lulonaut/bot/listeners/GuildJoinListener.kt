package de.lulonaut.bot.listeners

import de.lulonaut.bot.Main
import de.lulonaut.bot.utils.Database
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildJoinListener : ListenerAdapter() {
    override fun onGuildJoin(event: GuildJoinEvent) {
        Database.saveConfig(event.guild.id, Main.constants.defaultOptions)
        println("loaded default options for a new guild")
    }
}