package de.lulonaut.bot.commands.messagecount

import de.lulonaut.bot.cache.DatabaseCache
import de.lulonaut.bot.utils.Conf
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class DeleteAllMessages : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        if (event.author.isBot) {
            return
        }
        val prefix = DatabaseCache.getConfig(event.guild.id)?.get("prefix")

        if (!msg[0].equals(prefix + "deleteMessages", ignoreCase = true)) {
            return
        }

        val eb = EmbedBuilder()
        val perms = Objects.requireNonNull(event.member)!!.permissions
        if (!perms.contains(Permission.MANAGE_SERVER)) {
            eb.setTitle("Error!")
            eb.setDescription("You don't have the `Manage Server` permission.")
            event.channel.sendMessage(eb.build()).queue()
            return
        }
        //user has perms
        event.channel.sendMessage("Please type \"confirm\" to reset the counter for this server. **THIS CANNOT BE UNDONE**")
            .queue()
        val dsm = DeletingStateMachine(event.channel, event.member!!.user)
        event.jda.addEventListener(dsm)
    }
}