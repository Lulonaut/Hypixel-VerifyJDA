package de.lulonaut.bot.commands.messagecount

import de.lulonaut.bot.Main
import java.lang.Exception
import de.lulonaut.bot.utils.Database
import java.lang.NullPointerException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.EmbedBuilder
import de.lulonaut.bot.commands.Aliases.LookupUserAliases
import java.util.stream.Collectors
import java.util.*
import java.util.stream.Stream

class LookupUser : ListenerAdapter() {
    var aliases = Stream.of(*LookupUserAliases.values())
        .map { obj: LookupUserAliases -> obj.name }
        .collect(Collectors.toList())

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        try {
            if (!aliases.contains(msg[0].substring(1))) {
                return
            }
        } catch (e: Exception) {
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
            val UserID = msg[1].replace("[^0-9]".toRegex(), "")
            if (UserID == "") {
                eb.setTitle("Error!")
                eb.setDescription("That user doesnt exist! Please @ them or use their UserID as the parameter.")
                event.channel.sendMessage(eb.build()).queue()
                return
            }
            try {
                Objects.requireNonNull(Main.jda!!.getUserById(UserID))!!.name
            } catch (e: NullPointerException) {
                eb.setTitle("Error!")
                eb.setDescription("That user doesnt exist or didn't send any messages yet.\nPlease @ them or use their UserID as the parameter.")
                event.channel.sendMessage(eb.build()).queue()
                return
            }
            eb.setTitle("Success")
            eb.setDescription(
                "<@!" + UserID + "> currently has " + Database.lookupUser(
                    event.guild.id,
                    UserID
                ) + " messages in this server"
            )
            event.channel.sendMessage(eb.build()).queue()
        }
    }
}