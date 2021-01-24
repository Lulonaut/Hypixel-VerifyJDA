package de.lulonaut.bot.listeners

import de.lulonaut.bot.Main
import de.lulonaut.bot.utils.Cache
import de.lulonaut.bot.utils.Constants
import de.lulonaut.bot.utils.Database
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class MessageListener : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (Objects.requireNonNull(event.member)!!.user.isBot) {
            return
        }
        if (Cache.getConfig(event.guild.id)?.get("counting") == "true") {
            Database.addMessage(event.guild.id, event.member!!.user.id)
        }
    }
}