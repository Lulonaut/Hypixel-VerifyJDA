package de.lulonaut.bot.commands.messagecount

import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.EmbedBuilder
import java.util.stream.Collectors
import de.lulonaut.bot.commands.Aliases.DeleteMessagesAliases
import net.dv8tion.jda.api.Permission
import java.util.*
import java.util.stream.Stream

class DeleteAllMessages : ListenerAdapter() {
    private var aliases: MutableList<String> = Stream.of(*DeleteMessagesAliases.values())
        .map { obj: DeleteMessagesAliases -> obj.name }
        .collect(Collectors.toList())

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        if (event.author.isBot) {
            return
        }
        if (!aliases.contains(msg[0].substring(1))) {
            return
        }
        val eb = EmbedBuilder()
        val perms = Objects.requireNonNull(event.member)!!.permissions
        if (!perms.contains(Permission.MANAGE_SERVER)) {
            eb.setTitle("Error!")
            eb.setDescription("")
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