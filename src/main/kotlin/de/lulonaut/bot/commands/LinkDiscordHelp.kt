package de.lulonaut.bot.commands

import de.lulonaut.bot.utils.Conf
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class LinkDiscordHelp : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        if (msg[0].equals(Conf.PREFIX + "linkdc", ignoreCase = true)) {
            event.channel.sendMessage(
                """
                    1.Go to /lobby on Hypixel
                    2.Click on your Head (second Slot)
                    3.Click on the twitter symbol (column 3, row 4)
                    4.Click on the Discord Symbol (second to last one) and paste your Discord Link in the Chat
                    https://gfycat.com/dentaltemptingleonberger (Stolen video)
                    """.trimIndent()
            ).queue()
        }
    }
}