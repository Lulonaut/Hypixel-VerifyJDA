package de.lulonaut.bot.commands.config

import de.lulonaut.bot.cache.DatabaseCache
import de.lulonaut.bot.utils.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ConfigStateMachine(channel: MessageChannel, event: GuildMessageReceivedEvent, member: Member?) :
    ListenerAdapter() {
    private val channelID: Long = channel.idLong
    private val userID: Long = member!!.idLong
    private var options: MutableMap<String?, String?>? = HashMap()
    var done: Boolean
    private val initEvent: GuildMessageReceivedEvent
    private var currentStep: Int = 1
    private var inProgress = false


    private fun showCurrentConfig(): String {
        val sb = StringBuilder()

        val gmRole: Boolean = this.options?.get("guildMemberRole") == "true"
        sb.append("Verified Role: `").append(this.options?.get("role")).append("`\n")
        sb.append("Rank roles: `").append(this.options?.get("rankRoles")).append("`\n")
        sb.append("Message counting system: `").append(this.options?.get("counting")).append("`\n")
        sb.append("Guild member role: `").append(this.options?.get("guildMemberRole")).append("`\n")
        if (gmRole) {
            sb.append("Guild Name: `").append(this.options?.get("guildID")).append("`\n")
            sb.append("Guild member role: `").append(this.options?.get("guildRoleName")).append("`\n")
        }
        sb.append("Command prefix: `").append(this.options?.get("prefix")).append("`")
        return sb.toString()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.channel.idLong != channelID || event.author.idLong != userID) {
            return
        }
        if (event.message.contentRaw.equals("continue", ignoreCase = true) && !inProgress) {
            currentStep++
        }
        if (event.message.contentRaw.equals("exit", ignoreCase = true) && !inProgress) {
            Database.saveConfig(event.guild.id, this.options)
            val eb = EmbedBuilder()
            eb.setTitle("Setup closed")
            eb.setDescription(
                "Config saved to Database.\n" +
                        "Current config for ${event.guild.name}:\n" +
                        showCurrentConfig()
            )
            event.channel.sendMessage(eb.build()).queue()
            event.jda.removeEventListener(this)
            this.options = null
            return
        }
        when (currentStep) {
            1 ->                 //verify role
                if (inProgress) {
                    this.options?.set("role", event.message.contentRaw)
                    event.channel.sendMessage(
                        """
    OK: Verify Role is now set to: `${event.message.contentRaw}` Please make sure this role exists before proceeding
    Type `continue` to continue with the setup or  `exit` to save the config.
    """.trimIndent()
                    ).queue()
                    inProgress = false
                } else {
                    event.channel.sendMessage(
                        """
    Welcome to the config setup
    Please specify a Role users get when verifying successfully. This is currently set to `${this.options?.get("role")}`
    """.trimIndent()
                    ).queue()
                    inProgress = true
                }
            2 ->                 //message counting
                if (inProgress) {
                    when {
                        event.message.contentRaw.equals("enabled", ignoreCase = true) -> {
                            this.options?.set("counting", "true")
                            event.channel.sendMessage(
                                "OK: Message counting system is now enabled. " +
                                        "With it come some commands:\n" +
                                        "${Conf.PREFIX}`leaderboard`: shows the current leaderboard of users with the most messages\n" +
                                        "${Conf.PREFIX}`check <user>`: shows how many messages you or a given user currently have\n" +
                                        "${Conf.PREFIX}`deleteMessages`: this command requires the \"Manage Server\" Permission and clears the current message counter for everyone in the server. (further confirmation is required)\n" +

                                        "Type `continue` to continue with the setup or  `exit` to save the config"
                            ).queue()
                            inProgress = false
                        }
                        event.message.contentRaw.equals("disabled", ignoreCase = true) -> {
                            this.options?.set("counting", "false")

                            event.channel.sendMessage(
                                "OK: message counting system is now disabled\n" +
                                        "Type `continue` to continue with the setup or  `exit` to save the config"
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
                            this.options?.set("guildMemberRole", "true")
                            event.channel.sendMessage(
                                """
                    OK: guild member role enabled. In the next step you will have to set a guild name. If you skip this step the feature will not work.
                    Type `continue` to continue with the setup or  `exit` to save the config
                    """.trimIndent()
                            ).queue()
                            inProgress = false
                            return
                        }
                        event.message.contentRaw.equals("disabled", ignoreCase = true) -> {
                            this.options?.set("guildMemberRole", "false")
                            event.channel.sendMessage(
                                """
                    OK: guild member role disabled.
                    Type `continue` to continue with the setup or  `exit` to save the config
                    """.trimIndent()
                            ).queue()
                            currentStep += 2
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
                        guildName = API.getGuild(event.message.contentRaw)
                    } catch (e: Exception) {
                        event.channel.sendMessage(
                            """
    An error occurred while validating the guild, defaulting to no guild role for now. Please try again later if you think this is a mistake
    Type `continue` to continue with the setup or  `exit` to save the config
    """.trimIndent()
                        ).queue()
                        this.options?.set("guildMemberRole", "disabled")
                        inProgress = false
                        return
                    }
                    if (guildName == "") {
                        event.channel.sendMessage(
                            """
    Either this user is not in a guild or an error occurred while checking this. Please try again later if you believe this is a mistake. Defaulting to no guild role for now.
    Type `continue` to continue with the setup or  `exit` to save the config
    """.trimIndent()
                        ).queue()
                        this.options?.set("guildMemberRole", "disabled")
                        currentStep++
                    } else {
                        this.options?.set("guildID", guildName)
                        event.channel.sendMessage(
                            """
    OK: Guild Name is now set to: `$guildName` Now you need to set a role players get when they are in the guild.
    Type `continue` to continue with the setup or  `exit` to save the config
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
                    this.options?.set("guildRoleName", event.message.contentRaw)
                    event.channel.sendMessage(
                        "OK: Guild Member role is now set to: `${event.message.contentRaw}`\n" +
                                "Type `continue` to continue with the setup or  `exit` to save the config"
                    ).queue()
                    inProgress = false
                } else {
                    event.channel.sendMessage("Please provide a role that is assigned to players that are in your guild.")
                        .queue()
                    inProgress = true
                }
            }
            6 -> {      //rank role toggle
                val message: String = event.message.contentRaw
                if (inProgress) {
                    if (message != "enabled" && message != "disabled") {
                        event.channel.sendMessage("Invalid input : `${event.message.contentRaw}`. Choose either `enabled` or `disabled`")
                            .queue()
                        return
                    } else {
                        if (message == "enabled") {
                            this.options?.set("rankRoles", "true")
                        } else {
                            this.options?.set("rankRoles", "false")
                        }
                    }
                    event.channel.sendMessage(
                        "OK: Rank roles are now ${message}\n" + "Type `continue` to continue with the setup or  `exit` to save the config"
                    ).queue()
                    inProgress = false
                } else {
                    event.channel.sendMessage(
                        "The bot can check the current rank of a user and give them the role for this rank." +
                                "This requires the following roles to be created:\n" +
                                "`VIP`\n" +
                                "`VIP+`\n" +
                                "`MVP`\n" +
                                "`MVP+`\n" +
                                "`MVP++`\n\n" +
                                "If you want this enabled type `enabled` otherwise `disabled`."
                    ).queue()
                    inProgress = true
                }
            }
            7 -> {      //prefix
                inProgress = if (inProgress) {
                    this.options?.set("prefix", event.message.contentRaw)
                    event.channel.sendMessage(
                        "OK: Prefix is now set to: `${event.message.contentRaw}`." +
                                "Type `continue` to continue with the setup or  `exit` to save the config"
                    ).queue()
                    false
                } else {
                    val currentPrefix = this.options?.get("prefix")
                    event.channel.sendMessage(
                        "Now please set a prefix to be used." +
                                "The current Prefix is `$currentPrefix`\n"
                    ).queue()
                    true
                }
            }

            8 -> {
                //final message
                Database.saveConfig(event.guild.id, this.options)
                DatabaseCache.refreshOrAddCache(event.guild.id)
                val eb = EmbedBuilder()
                eb.setTitle("Setup finished")
                eb.setDescription(
                    """
    These are the current config this.options for ${event.guild.name}:
    ${showCurrentConfig()}
    """.trimIndent()
                )
                event.channel.sendMessage(eb.build()).queue()
                done = true
                this.options = null
                event.jda.removeEventListener(this)
            }
            else -> {
                event.channel.sendMessage("Invalid input.")
            }
        }
    }

    /*
    Steps:
    1: Verified role
    2: message counting system toggle
    3: guild member role toggle
    4: if 3: guild name (validated with MC name)
    5: rank roles toggle
    6: command prefix
     */
    init {
        //        this.guildID = guild.getIdLong();
        done = false
        initEvent = event
        //default this.options
        this.options = DatabaseCache.getConfig(event.guild.id) as MutableMap<String?, String?>?
        if (!this.options?.contains("prefix")!!) {
            this.options?.set("role", "Hypixel Verified")
            this.options?.set("counting", "false")
            this.options?.set("guildMemberRole", "false")
            this.options?.set("guildID", "")
            this.options?.set("prefix", Conf.PREFIX)
            this.options?.set("rankRoles", "false")
        }

    }
}