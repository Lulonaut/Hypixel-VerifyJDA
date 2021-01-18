package de.lulonaut.bot.utils

import de.lulonaut.bot.errors.ConfigException
import de.lulonaut.bot.errors.ConfigNotFoundException
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess

object Conf {
    //All the variables loaded in the config
    lateinit var PREFIX: String
    lateinit var VerifyRole: String
    var RankRoles = false
    var GuildRoles = false
    lateinit var Guild: String
    lateinit var GuildRole: String
    lateinit var Endpoint: String
    lateinit var APIKey: String

    /**
     * Util Function that loads all the Config values to the variables
     */
    @Throws(IOException::class, AssertionError::class)
    fun loadConf() {
        try {
            println("trying to load....")
            PREFIX = Config.getConf("prefix", false).toString()
            println(PREFIX)
            VerifyRole = Config.getConf("role", false).toString()
            println(VerifyRole)
            Endpoint = Config.getConf("apiendpoint", true).toString()
            println(Endpoint)
            println("looks good...")
            if (!Endpoint.equals("hypixel", ignoreCase = true) && !Endpoint.equals("slothpixel", ignoreCase = true)) {
                println("Invalid API endpoint, choose either Hypixel or Slothpixel. (Defaulting to Slothpixel)")
                Endpoint = "slothpixel"
            } else if (Endpoint.equals("hypixel", ignoreCase = true)) {
                APIKey = Config.getConf("hypixelapikey", true).toString()
            }
            if (Objects.requireNonNull(Config.getConf("rankroles", true)).equals("true", ignoreCase = true)) {
                RankRoles = true
                println(RankRoles)
            }
            if (Objects.requireNonNull(Config.getConf("guildroletoggle", false)).equals("true", ignoreCase = true)) {
                GuildRoles = true
                println(GuildRoles)
            }
            if (Config.getConf("guild", true) != null) {
                Guild = Config.getConf("guild", true).toString()
                println(Guild)
            }
            if (Config.getConf("guildrole", true) != null) {
                GuildRole = Config.getConf("guildrole", true).toString()
                println(GuildRole)
            }
        } catch (e: ConfigException) {
            e.printStackTrace()
            exitProcess(1)
        } catch (e: ConfigNotFoundException) {
            e.printStackTrace()
            exitProcess(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}