package de.lulonaut.bot.commands

import de.lulonaut.bot.utils.API
import de.lulonaut.bot.utils.Cache
import de.lulonaut.bot.utils.Conf
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.exceptions.HierarchyException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*


class Verify : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        //Checking if it's the actual command
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        val apiResult: Array<String?>?
        val userDiscord = Objects.requireNonNull(event.member)!!.user.asTag
        val prefix = Cache.getConfig(event.guild.id)?.get("prefix")

        if (!msg[0].equals(prefix + "verify", ignoreCase = true)) {
            return
        }
        //Checking correct usage
        if (msg.size != 2) {
            event.channel.sendMessage("Usage: " + prefix + "verify [Minecraft IGN]").queue()
            return
        }

        //Command logic
        try {
            //Getting the linked Discord + Error handling
            apiResult = API.getStuff(msg[1], Conf.Endpoint)
            if (apiResult!![0] == "error") {
                event.channel.sendMessage(
                    "There was an Error while checking your linked Discord, " +
                            "please try again later! (API probably down)"
                ).queue()
                return
            }
        } catch (e: Exception) {
            event.channel.sendMessage(
                "Some error occurred, " +
                        "API is probably down. Please try again later"
            ).queue()
            return
        }
        // Discord obtained, checking if it matches their Discord
        val discord = apiResult[0]
        val nickname = apiResult[1]
        var rank = apiResult[2]
        val guild = apiResult[3]
        var errorCount = 0
        var errors = 0

        //Case: Discord is null (not Linked anything)
        when {
            discord == "null" -> {
                event.channel.sendMessage(
                    "Looks you didn't link a Discord yet. If you don't know how to " +
                            "add one please type '" + prefix + "linkdc'. If you just changed this please wait a few " +
                            "minutes and try again. (Spamming it won't do anything)"
                ).queue()
            }
            userDiscord != discord -> {
                event.channel.sendMessage(
                    "Your Discord Tag is: `" + userDiscord + "`. But a wise man told me " +
                            "you linked this Discord in Minecraft: `" + discord + "`. If you just changed this please wait a " +
                            "few minutes and try again. (Spamming it won't do anything)"
                ).queue()
            }
            else -> {
                try {
                    //Add Role(s)
                    event.guild.addRoleToMember(event.member!!, event.guild.getRolesByName(Conf.VerifyRole!!, false)[0])
                        .queue()
                    if (Conf.OptionalRole != null) {
                        event.guild.addRoleToMember(
                            event.member!!,
                            event.guild.getRolesByName(Conf.OptionalRole!!, false)[0]
                        ).queue()
                    }

                    //Change Nickname
                    event.member!!.modifyNickname(nickname).queue()

                    //Add Role for Rank if enabled
                    if (Conf.RankRoles) {
                        when (rank) {
                            "VIP_PLUS" -> rank = "VIP+"
                            "MVP_PLUS" -> rank = "MVP+"
                            "MVP_PLUS_PLUS" -> rank = "MVP++"
                        }
                        try {
                            if (rank != "null") {
                                event.guild.addRoleToMember(event.member!!, event.guild.getRolesByName(rank!!, true)[0])
                                    .queue()
                            }
                        } catch (e: Exception) {
                            event.channel.sendMessage("Looks like a rank role does not exist, please ask a Staff Member to add the following Role: `$rank`")
                                .queue()
                        }
                    }
                    if (Conf.GuildRoles && guild == Conf.Guild) {
                        try {
                            event.guild.addRoleToMember(
                                event.member!!,
                                event.guild.getRolesByName(Conf.GuildRole!!, true)[0]
                            ).queue()
                        } catch (e: Exception) {
                            event.channel.sendMessage("Looks like a role called " + Conf.GuildRole + " doesn't exist. Please ask an Admin to add it!")
                                .queue()
                        }
                    }
                } catch (e: HierarchyException) {
                    errorCount++
                } catch (e: Exception) {
                    e.printStackTrace()
                    errors++
                }
                when {
                    errorCount > 0 -> {
                        event.channel.sendMessage("You have higher Perms than me so i couldn't change much. But your Discord matches the one linked on Minecraft :D")
                            .queue()
                    }
                    errors > 0 -> {
                        event.channel.sendMessage("Some internal error happened, please contact a Admin :(").queue()
                    }
                    else -> {
                        event.channel.sendMessage("You now have the " + Conf.VerifyRole + " Role. (and maybe some more)")
                            .queue()
                    }
                }
            }
        }
    }
}