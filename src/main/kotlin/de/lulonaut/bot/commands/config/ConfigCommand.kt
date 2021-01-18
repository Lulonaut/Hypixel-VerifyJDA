package de.lulonaut.bot.commands.config

import de.lulonaut.bot.utils.Cache
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class ConfigCommand : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val prefix = Cache.getConfig(event.guild.id)?.get("prefix")
        if (event.author.isBot || !event.message.contentRaw.equals(prefix + "config", ignoreCase = true)) {
            return
        } else {
            if (!Objects.requireNonNull(event.member)!!.permissions.contains(Permission.MANAGE_SERVER)) {
                val eb = EmbedBuilder()
                eb.setTitle("Error!")
                eb.setDescription("To use this command you need to have the \"Manage Server\" permission!")
                event.channel.sendMessage(eb.build()).queue()
                return
            }
            println(event.message.contentRaw)
            println("starting state machine")
            val csm = ConfigStateMachine(event.channel, event, event.member)
            event.jda.addEventListener(csm)
            Thread {
                try {
                    //timeout of 5 minutes
                    println("going to sleep")
                    Thread.sleep(300000)
                    println("woke up")
                    println(csm.done)
                    if (!csm.done) {
                        println("not done, cancelling")
                        event.channel.sendMessage("Timeout reached, canceling config.").queue()
                        event.jda.removeEventListener(csm)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}