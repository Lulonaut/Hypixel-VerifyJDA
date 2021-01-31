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

        val config = Cache.getConfig(event.guild.id)
        val prefix = config?.get("prefix")
        val msg = event.message.contentRaw.split(" ").toTypedArray()
        //checking if it's the actual command
        if (!msg[0].equals(prefix + "verify", ignoreCase = true)) {
            return
        }

        //assigning variables that can already be assigned
        val apiResult: Array<String?>?
        val userDiscord = Objects.requireNonNull(event.member)!!.user.asTag
        val rankRoles: Boolean = config?.get("rankRoles") == "true"
        val minecraftGuildRoleToggle: Boolean = config?.get("guildMemberRole") == "true"
        val verifyRole: String = config?.get("role").toString()
        var minecraftGuild = ""
        var minecraftGuildRole = ""
        if (minecraftGuildRoleToggle) {
            minecraftGuild = config?.get("guildID").toString()
            minecraftGuildRole = config?.get("guildRoleName").toString()
        }
        //Checking correct usage
        if (msg.size != 2) {
            event.channel.sendMessage("Usage: ${prefix}verify [Minecraft IGN]").queue()
            return
        }
        //checking possible config error
        if (minecraftGuildRoleToggle && minecraftGuild == "null") {
            event.channel.sendMessage("WARNING: Guild Member roles are enabled but no guild was set. Please ask a staff member to rerun the config with `${prefix}config`\nThis will probably result in an error!")
                .queue()
        }
        if (minecraftGuildRoleToggle && minecraftGuildRole == "null") {
            event.channel.sendMessage("WARNING: Guild Member roles are enabled but no role for Guild Members was set. Please ask a staff member to rerun the config with `${prefix}config`\nThis will probably result in an error!")
                .queue()
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
                        "API is probably down. Please try again later!"
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


        when {
            //Case 1: Discord is null (not Linked anything)
            discord == "null" -> {
                event.channel.sendMessage(
                    "Looks you didn't link a Discord yet. If you don't know how to " +
                            "add one please type '${prefix}linkdc'. If you just changed this please wait a few " +
                            "minutes and try again. (Spamming it won't do anything)"
                ).queue()
            }
            //Case 2: Discord was linked but doesn't match users discord
            userDiscord != discord -> {
                event.channel.sendMessage(
                    "Your Discord Tag is: `" + userDiscord + "`. But a wise man told me " +
                            "you linked this Discord in Minecraft: `" + discord + "`. If you just changed this please wait a " +
                            "few minutes and try again. (Spamming it won't do anything)"
                ).queue()
            }
            //Case 3: Discord does match (no if statement because it's the only outcome possible)
            else -> {
                try {
                    //Add Role
                    event.guild.addRoleToMember(event.member!!, event.guild.getRolesByName(verifyRole, false)[0])
                        .queue()
                    //Change Nickname
                    event.member!!.modifyNickname(nickname).queue()
                } catch (e: HierarchyException) {
                    errorCount++
                } catch (e: IndexOutOfBoundsException) {
                    event.channel.sendMessage("Looks like the verified role doesn't exist, please ask a Staff Member to add the following Role: `${verifyRole}`")
                        .queue()
                }


                //Add Role for Rank if enabled
                if (rankRoles) {
                    //change rank names from API to their more readable equivalent
                    when (rank) {
                        "VIP_PLUS" -> rank = "VIP+"
                        "MVP_PLUS" -> rank = "MVP+"
                        "MVP_PLUS_PLUS" -> rank = "MVP++"
                    }
                    try {
                        //if rank is null the player has no rank -> no rank role needed, else assign rank role
                        if (rank != "null") {
                            event.guild.addRoleToMember(event.member!!, event.guild.getRolesByName(rank!!, true)[0])
                                .queue()
                        }
                    } catch (e: HierarchyException) {
                        errorCount++
                    } catch (e: IndexOutOfBoundsException) {
                        event.channel.sendMessage("Looks like a role for your rank does not exist, please ask a Staff Member to add the following Role: `$rank`")
                            .queue()
                    } catch (e: Exception) {
                        errors++
                    }
                }
                //if guild member roles are enabled and it matches the guild assign the member role
                if (minecraftGuildRoleToggle && guild == minecraftGuild) {
                    try {
                        event.guild.addRoleToMember(
                            event.member!!,
                            event.guild.getRolesByName(minecraftGuildRole, true)[0]
                        ).queue()
                    } catch (e: HierarchyException) {
                        errorCount++
                    } catch (e: IndexOutOfBoundsException) {
                        event.channel.sendMessage("Looks like the role for guild members does not exist, please ask a Staff Member to add the following Role: `$minecraftGuild`")
                            .queue()
                    } catch (e: Exception) {
                        errors++
                    }
                }
                when {
                    errorCount > 0 -> {
                        event.channel.sendMessage("The Bot can't assign roles to users with higher roles then itself.")
                            .queue()
                    }
                    errors > 0 -> {
                        event.channel.sendMessage(
                            "Some Error occurred and it doesn't look like it's your fault. Please report this to Lulonaut#3350 on Discord, thanks. \n" +
                                    "If everything worked like it should ignore this message."
                        ).queue()
                    }
                    else -> {
                        event.channel.sendMessage("You now have the `${verifyRole}` role.").queue()
                    }
                }
            }
        }
    }
}