package de.lulonaut.bot.listeners

import de.lulonaut.bot.utils.Database
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*

class MessageListener : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (Objects.requireNonNull(event.member)!!.user.isBot) {
            return
        }
        Database.addMessage(event.guild.id, event.member!!.user.id)
    }
}