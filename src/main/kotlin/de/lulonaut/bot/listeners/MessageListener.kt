package de.lulonaut.bot.listeners

import de.lulonaut.bot.cache.DatabaseCache
import de.lulonaut.bot.cache.MessageCache
import de.lulonaut.bot.utils.Database
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MessageListener : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.member?.user?.isBot == true) return
        if (DatabaseCache.getConfig(event.guild.id)?.get("counting") == "true") {
            //add message to db
            Database.addMessage(event.guild.id, event.member!!.user.id)
            //add userId to cache to handle message deletion
            MessageCache.addMessage(event.messageIdLong, event.author.idLong)
            println("added to cache")
        }
    }

    override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        val userID = MessageCache.getUserId(event.messageIdLong)
        println("User ID: $userID")
        if (DatabaseCache.getConfig(event.guild.id)?.get("counting") == "true" && userID != 0L) {
            println("Removing message!")
            Database.removeMessage(event.guild.id, userID.toString())
        }
    }
}