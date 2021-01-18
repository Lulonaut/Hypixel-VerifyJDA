package de.lulonaut.bot.commands.messagecount

import java.lang.Exception
import de.lulonaut.bot.utils.Database
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.EmbedBuilder
import java.util.stream.Collectors
import de.lulonaut.bot.commands.Aliases.LeaderboardAliases
import java.util.LinkedList
import java.lang.StringBuilder
import java.util.stream.Stream

class MessageLeaderboard : ListenerAdapter() {
    private var aliases: MutableList<String> = Stream.of(*LeaderboardAliases.values())
        .map { obj: LeaderboardAliases -> obj.name }
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
        val users = LinkedList<String?>()
        val messages = LinkedList<Int?>()
        val leaderboard = Database.sort(event.guild.id)
        for ((key, value) in leaderboard) {
            users.add(key)
            messages.add(value)
        }
        if (users.size > 10) {
            users.subList(9, users.size)
        }
        val lb = StringBuilder()
        for (i in users.indices) {
            lb.append("<@!")
            lb.append(users[i])
            lb.append(">")
            lb.append(" has ")
            lb.append(messages[i])
            lb.append(" messages and is Place ")
            lb.append(i + 1)
            lb.append("\n")
        }
        val eb = EmbedBuilder()
            .setTitle("Current Leaderboard for " + event.guild.name, null)
            .setDescription(lb.toString())
        event.channel.sendMessage(eb.build()).queue()
    }
}