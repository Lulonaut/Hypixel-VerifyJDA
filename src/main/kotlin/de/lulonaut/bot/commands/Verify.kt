package de.lulonaut.bot.commands

import java.lang.Exception
import de.lulonaut.bot.utils.Conf
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.exceptions.HierarchyException
import de.lulonaut.bot.utils.API
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * <h1>Verify Command</h1>
 * This Command takes a Username and then checks their Linked Discord on Hypixel.
 * If it matches their Discord Tag they get a role and some other stuff happens depending on the Config
 *
 * @see Config
 */
class Verify : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        //set logging level
        Logger.getLogger("").level = Level.INFO
        Logger.getLogger("").handlers[0].level = Level.INFO


        //Checking if it's the actual command
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        val APIResult: Array<String?>?
        val UserDiscord = Objects.requireNonNull(event.member)!!.user.asTag
        if (!msg[0].equals(Conf.PREFIX + "verify", ignoreCase = true)) {
            return
        }
        //Checking correct usage
        if (msg.size != 2) {
            event.channel.sendMessage("Usage: " + Conf.PREFIX + "verify [Minecraft IGN]").queue()
            return
        }

        //Command logic
        try {
            //Getting the linked Discord + Error handling
            APIResult = API.getStuff(msg[1], Conf.Endpoint)
            if (APIResult!![0] == "error") {
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
        val Discord = APIResult[0]
        val Nickname = APIResult[1]
        var Rank = APIResult[2]
        val Guild = APIResult[3]
        println(Guild)
        var ErrorCount = 0
        var Errors = 0

        //Case: Discord is null (not Linked anything)
        if (Discord == "null") {
            event.channel.sendMessage(
                "Looks you didn't link a Discord yet. If you don't know how to " +
                        "add one please type '" + Conf.PREFIX + "linkdc'. If you just changed this please wait a few " +
                        "minutes and try again. (Spamming it won't do anything)"
            ).queue()
        } else if (UserDiscord != Discord) {
            event.channel.sendMessage(
                "Your Discord Tag is: `" + UserDiscord + "`. But a wise man told me " +
                        "you linked this Discord in Minecraft: `" + Discord + "`. If you just changed this please wait a " +
                        "few minutes and try again. (Spamming it won't do anything)"
            ).queue()
        } else {
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
                event.member!!.modifyNickname(Nickname).queue()

                //Add Role for Rank if enabled
                if (Conf.RankRoles) {
                    when (Rank) {
                        "VIP_PLUS" -> Rank = "VIP+"
                        "MVP_PLUS" -> Rank = "MVP+"
                        "MVP_PLUS_PLUS" -> Rank = "MVP++"
                    }
                    try {
                        if (Rank != "null") {
                            event.guild.addRoleToMember(event.member!!, event.guild.getRolesByName(Rank!!, true)[0])
                                .queue()
                        }
                    } catch (e: Exception) {
                        event.channel.sendMessage("Looks like a rank role does not exist, please ask a Staff Member to add the following Role: `$Rank`")
                            .queue()
                    }
                }
                if (Conf.GuildRoles && Guild == Conf.Guild) {
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
                ErrorCount++
            } catch (e: Exception) {
                e.printStackTrace()
                Errors++
            }
            if (ErrorCount > 0) {
                event.channel.sendMessage("You have higher Perms than me so i couldn't change much. But your Discord matches the one linked on Minecraft :D")
                    .queue()
            } else if (Errors > 0) {
                event.channel.sendMessage("Some internal error happened, please contact a Admin :(").queue()
            } else {
                event.channel.sendMessage("You now have the " + Conf.VerifyRole + " Role. (and maybe some more)")
                    .queue()
            }
        }
    }
}