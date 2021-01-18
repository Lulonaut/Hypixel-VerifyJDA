package de.lulonaut.bot.commands.messagecount

import de.lulonaut.bot.utils.Database
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.entities.User

class DeletingStateMachine(channel: MessageChannel, author: User) : ListenerAdapter() {
    private val channelID: Long = channel.idLong
    private val userID: Long = author.idLong
    private var success = false
    private var usages: Int = 0
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }
        if (event.channel.idLong != channelID) {
            return
        }
        if (event.author.idLong != userID) {
            return
        }

        //actual logic
        if (!event.message.contentRaw.equals("confirm", ignoreCase = true)) {
            //the first time it fires is the command being executed, can't be "confirm"
            usages += 1
            if (usages == 1) {
                return
            }
            event.channel.sendMessage("Not resetting.").queue()
            success = false
        } else {
            Database.removeAllMessages(event.guild.id)
            event.channel.sendMessage("Counter reset.").queue()
            success = true
        }
        //remove event listener when its done
        event.jda.removeEventListener(this)
    }

}