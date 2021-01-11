package de.lulonaut.bot.commands

import de.lulonaut.bot.utils.Conf
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class Calculate : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        if (msg[0].equals(Conf.PREFIX + "calculate", ignoreCase = true)) {
            if (msg.size != 4) {
                event.channel.sendMessage("Missing Parameters! Usage: " + Conf.PREFIX + " [add/sub] [first-num] [second-num]")
                    .queue()
                return
            }
            val num1 = msg[2].toInt().toFloat()
            val num2 = msg[3].toInt().toFloat()
            val equals: Float
            when (msg[1]) {
                "add" -> {
                    equals = num1 + num2
                    event.channel.sendMessage("$num1 + $num2 equals: $equals").queue()
                    return
                }
                "sub" -> {
                    equals = num1 - num2
                    event.channel.sendMessage("$num1 - $num2 equals: $equals").queue()
                    return
                }
                else -> event.channel.sendMessage("Wrong Parameters! Usage: .calculate [add/sub] [first-num] [second-num")
                    .queue()
            }
        }
    }
}