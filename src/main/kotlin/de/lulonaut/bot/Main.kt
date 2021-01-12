package de.lulonaut.bot

import de.lulonaut.bot.commands.Calculate
import de.lulonaut.bot.commands.LinkDiscordHelp
import de.lulonaut.bot.commands.Verify
import de.lulonaut.bot.commands.config.ConfigCommand
import de.lulonaut.bot.commands.messagecount.DeleteAllMessages
import de.lulonaut.bot.commands.messagecount.LookupUser
import de.lulonaut.bot.commands.messagecount.MessageLeaderboard
import de.lulonaut.bot.listeners.MessageListener
import de.lulonaut.bot.utils.Conf
import de.lulonaut.bot.utils.Config
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess


object Main {
    var jda: JDA? = null

    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        TimeUnit.SECONDS.sleep(2)
        Conf.loadConf()
        println("Prefix from Config set to: " + Conf.PREFIX)
        if (Conf.RankRoles) {
            println("You enabled rank roles. Please make sure the following roles exist: VIP, VIP+, MVP, MVP+, MVP++. Otherwise there will be constant Errors")
        }
        println("Config loaded.")
        try {
            //start bot with token
            jda = JDABuilder.createDefault(Config.getConf("token", false))
                .setActivity(Activity.playing("https://github.com/Lulonaut/Hypixel-VerifyJDA"))
                .build()
        } catch (e: LoginException) {
            println("The Token is invalid! Please check your config.")
            exitProcess(1)
        } catch (e: Exception) {
            println("There was an error while logging in, please try again and check your config!")
            e.printStackTrace()
            exitProcess(1)
        }
        registerEvents()
        println("All Events registered!")
        registerCommands()
        println("All Commands registered!")
    }

    private fun registerEvents() {
        jda!!.addEventListener(MessageListener())
    }

    private fun registerCommands() {
        jda!!.addEventListener(Calculate())
        jda!!.addEventListener(Verify())
        jda!!.addEventListener(LinkDiscordHelp())
        jda!!.addEventListener(MessageLeaderboard())
        jda!!.addEventListener(LookupUser())
        jda!!.addEventListener(DeleteAllMessages())
        jda!!.addEventListener(ConfigCommand())
    }
}