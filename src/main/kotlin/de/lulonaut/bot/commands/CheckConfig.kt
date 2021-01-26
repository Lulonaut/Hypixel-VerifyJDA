package de.lulonaut.bot.commands

import de.lulonaut.bot.utils.Cache
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CheckConfig : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {

        val config = Cache.getConfig(event.guild.id)
        val prefix = config?.get("prefix")
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        //checking if it's the actual command
        if (!msg[0].equals(prefix + "checkConfig", ignoreCase = true)) {
            return
        }

        val sb = StringBuilder()
        sb.append("These are the current config options for: ${event.guild.name}\n")
        val gmRole: Boolean = config?.get("guildMemberRole") == "true"
        sb.append("Verified Role: `").append(config?.get("role")).append("`\n")
        sb.append("Rank roles: `").append(config?.get("rankRoles")).append("`\n")
        sb.append("Message counting system: `").append(config?.get("counting")).append("`\n")
        sb.append("Guild member role: `").append(config?.get("guildMemberRole")).append("`\n")
        if (gmRole) {
            sb.append("Guild Name: `").append(config?.get("guildID")).append("`\n")
            sb.append("Guild member role: `").append(config?.get("guildRoleName")).append("`\n")
        }
        sb.append("Command prefix: `").append(config?.get("prefix")).append("`")

        val eb = EmbedBuilder()
        eb.setTitle("current Config")
        eb.setDescription(sb.toString())

        event.channel.sendMessage(eb.build()).queue()
    }

}