package de.lulonaut.bot.commands.config

import de.lulonaut.bot.utils.Cache
import de.lulonaut.bot.utils.Conf
import de.lulonaut.bot.utils.Database
import de.lulonaut.bot.utils.GetGuild
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class ConfigStateMachine(channel: MessageChannel, event: GuildMessageReceivedEvent, member: Member?) :
    ListenerAdapter() {
    private val channelID: Long = channel.idLong
    private val userID: Long = member!!.idLong
    private val options: MutableMap<String, String?> = HashMap()
    var done: Boolean
    private val initEvent: GuildMessageReceivedEvent
    private var currentStep: Int = 1
    private var inProgress = false


    private fun showCurrentConfig(): String {
        val sb = StringBuilder()
        val count: String = if (options["counting"] == "true") {
            "enabled"
        } else {
            "disabled"
        }
        val gRole: String
        val gmRole: Boolean
        if (options["guildMemberRole"] == "true") {
            gRole = "enabled"
            gmRole = true
        } else {
            gRole = "disabled"
            gmRole = false
        }
        sb.append("Verified Role: `").append(options["role"]).append("`\n")
        sb.append("Message counting system: `").append(count).append("`\n")
        sb.append("Guild member role: `").append(gRole).append("`\n")
        if (gmRole) {
            sb.append("Guild Name: `").append(options["guildID"]).append("`")
        }
        sb.append("Command prefix: `").append(options["prefix"]).append("`")
        return sb.toString()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.channel.idLong != channelID || event.author.idLong != userID) {
            return
        }
        if (event.message.contentRaw.equals("cancel", ignoreCase = true) && !inProgress) {
            event.channel.sendMessage("setup cancelled.").queue()
            event.jda.removeEventListener(this)
            return
        }
        if (event.message.contentRaw.equals("continue", ignoreCase = true) && !inProgress) {
            currentStep++
        }
        if (event.message.contentRaw.equals("exit", ignoreCase = true) && !inProgress) {
            Database.saveConfig(event.guild.id, options)
            //TODO: Write changes to Database (new method for saving and getting current config)
            event.channel.sendMessage(
                """
    Config saved to Database.
    Current config:${showCurrentConfig()}
    """.trimIndent()
            ).queue()
            event.jda.removeEventListener(this)
            return
        }
        when (currentStep) {
            1 ->                 //verify role
                if (inProgress) {
                    options["role"] = event.message.contentRaw
                    event.channel.sendMessage(
                        """
    OK: Verify Role is now set to: `${event.message.contentRaw}` Please make sure this role exists before proceeding
    Type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.
    """.trimIndent()
                    ).queue()
                    inProgress = false
                } else {
                    event.channel.sendMessage(
                        """
    Welcome to the config setup
    Please specify a Role users get when verifying successfully. This is currently set to `${options["role"]}`
    """.trimIndent()
                    ).queue()
                    inProgress = true
                }
            2 ->                 //message counting
                if (inProgress) {
                    when {
                        event.message.contentRaw.equals("enabled", ignoreCase = true) -> {
                            options["counting"] = "true"
                            event.channel.sendMessage(
                                """OK: Message counting system is now enabled.
    With it come some commands:
    ${Conf.PREFIX}`leaderboard`: shows the current leaderboard of users with the most messages
    ${Conf.PREFIX}`check <user>`: shows how many messages you or a given user currently have
    ${Conf.PREFIX}`deleteMessages`: this command requires the "Manage Server" Permission and clears the current message counter for everyone in the server. (further confirmation is required)
                
    Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving."""
                            ).queue()
                            inProgress = false
                        }
                        event.message.contentRaw.equals("disabled", ignoreCase = true) -> {
                            options["counting"] = "false"
                            event.channel.sendMessage(
                                """
                    OK: Message counting system is now disabled.
                    Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.
                    """.trimIndent()
                            ).queue()
                            inProgress = false
                        }
                        else -> {
                            event.channel.sendMessage("Invalid input, please type `enabled` or `disabled`.").queue()
                        }
                    }
                } else {
                    event.channel.sendMessage(
                        """
    The message counting system tracks every message from every user and allows for a leaderboard, individual checking for a user and resetting of the counter.
    type one of the following: `enabled` to enable it or `disabled` to disable it.
    """.trimIndent()
                    ).queue()
                    inProgress = true
                }
            3 ->                 //guild member role toggle
                if (inProgress) {
                    when {
                        event.message.contentRaw.equals("enabled", ignoreCase = true) -> {
                            options["guildMemberRole"] = "true"
                            event.channel.sendMessage(
                                """
                    OK: guild member role enabled. In the next step you will have to set a guild name. If you skip this step the feature will not work.
                    Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.
                    """.trimIndent()
                            ).queue()
                            inProgress = false
                            return
                        }
                        event.message.contentRaw.equals("disabled", ignoreCase = true) -> {
                            options["guildMemberRole"] = "false"
                            event.channel.sendMessage(
                                """
                    OK: guild member role disabled.
                    Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.
                    """.trimIndent()
                            ).queue()
                            currentStep++
                            inProgress = false
                            return
                        }
                        else -> {
                            event.channel.sendMessage("Invalid input, please type `enabled` or `disabled`.").queue()
                        }
                    }
                } else {
                    event.channel.sendMessage(
                        """
    Setting up a guild role allows you to skip giving each guild member a role by hand if you run a Discord for a guild. If you don't this feature is pretty useless.
    Type one of the following: `enabled` to enable it or `disabled` to disable it.
    """.trimIndent()
                    ).queue()
                    inProgress = true
                }
            4 ->                 //getting guild name
                if (inProgress) {
                    val guildName: String?
                    try {
                        guildName = GetGuild.getGuild(event.message.contentRaw)
                    } catch (e: Exception) {
                        event.channel.sendMessage(
                            """
    An error occurred while validating the guild, defaulting to no guild role for now. Please try again later if you think this is a mistake
    Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.
    """.trimIndent()
                        ).queue()
                        options["guildMemberRole"] = "disabled"
                        inProgress = false
                        return
                    }
                    if (guildName == "") {
                        event.channel.sendMessage(
                            """
    Either this user is not in a guild or an error occurred while checking this. Please try again later if you believe this is a mistake. Defaulting to no guild role for now.
    Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.
    """.trimIndent()
                        ).queue()
                        options["guildMemberRole"] = "disabled"
                    } else {
                        options["guildID"] = guildName
                        event.channel.sendMessage(
                            """
    OK: Guild Name is now set to: `$guildName`
    Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.
    """.trimIndent()
                        ).queue()
                    }
                    inProgress = false
                    return
                } else {
                    event.channel.sendMessage("Please type your Minecraft username to set your guild.").queue()
                    inProgress = true
                }
            5 -> {
                if (inProgress) {
                    options["prefix"] = event.message.contentRaw
                    event.channel.sendMessage(
                        "OK: Prefix is now set to: `${event.message.contentRaw}`." +
                                "Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving."
                    ).queue()
                    inProgress = false
                } else {
                    println("asking for input")
                    val currentPrefix = options["prefix"]
                    event.channel.sendMessage(
                        "Now please set a prefix to be used." +
                                "The current Prefix is `$currentPrefix`\n"
                    ).queue()
                    inProgress = true
                }
                println("done!")
            }
            6 -> {
                //final message
                println(options)
                Database.saveConfig(event.guild.id, options)
                Cache.refreshOrAddCache(event.guild.id)
                val eb = EmbedBuilder()
                eb.setTitle("Setup done")
                eb.setDescription(
                    """
    Setup finished.
    These are the current config options for ${event.guild.name}:
    ${showCurrentConfig()}
    """.trimIndent()
                )
                event.channel.sendMessage(eb.build()).queue()
                done = true
                event.jda.removeEventListener(this)
            }
        }
    }

    /*
    Steps:
    1: Verified role
    2: message counting system toggle
    3: guild member role toggle
    4: if 3: guild name (validated with MC name)
     */
    init {
        //        this.guildID = guild.getIdLong();
        done = false
        initEvent = event
        //default options
        options["role"] = "Hypixel Verified"
        options["counting"] = "false"
        options["guildMemberRole"] = "false"
        options["guildID"] = ""
        options["prefix"] = Conf.PREFIX
    }
}