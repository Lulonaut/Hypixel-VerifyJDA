package de.lulonaut.bot.commands.messagecount

import de.lulonaut.bot.Main
import de.lulonaut.bot.cache.DatabaseCache
import de.lulonaut.bot.utils.Database
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class LookupUser : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        val prefix = DatabaseCache.getConfig(event.guild.id)?.get("prefix")

        if (!msg[0].equals(prefix + "check", ignoreCase = true)) {
            return
        }
        if (msg.size != 2) {
            event.channel.sendMessage(
                event.author.name + " currently has " + Database.lookupUser(
                    event.guild.id,
                    event.author.id
                ) + " messages in this server!"
            ).queue()
        } else {
            val eb = EmbedBuilder()
            val userID = msg[1].replace("[^0-9]".toRegex(), "")
            if (userID == "") {
                eb.setTitle("Error!")
                eb.setDescription("That user doesnt exist! Please @ them or use their UserID as the parameter.")
                event.channel.sendMessage(eb.build()).queue()
                return
            }
            try {
                Objects.requireNonNull(Main.jda!!.getUserById(userID))!!.name
            } catch (e: NullPointerException) {
                eb.setTitle("Error!")
                eb.setDescription("That user doesnt exist or didn't send any messages yet.\nPlease @ them or use their UserID as the parameter.")
                event.channel.sendMessage(eb.build()).queue()
                return
            }
            eb.setTitle("Success")
            eb.setDescription(
                "<@!$userID> currently has " + Database.lookupUser(
                    event.guild.id,
                    userID
                ) + " messages in this server"
            )
            event.channel.sendMessage(eb.build()).queue()
        }
    }
}