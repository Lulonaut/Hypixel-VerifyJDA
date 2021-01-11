package de.lulonaut.bot.utils

import de.lulonaut.bot.errors.ConfigException
import de.lulonaut.bot.errors.ConfigNotFoundException
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess

object Conf {
    //All the variables loaded in the config
    var PREFIX: String? = null
    var VerifyRole: String? = null
    var OptionalRole: String? = null
    var RankRoles = false
    var GuildRoles = false
    var Guild: String? = null
    var GuildRole: String? = null
    var Endpoint // API Endpoint
            : String? = null
    var APIKey //Api Key for Hypixel API
            : String? = null

    /**
     * Util Function that loads all the Config values to the variables
     */
    @Throws(IOException::class, AssertionError::class)
    fun loadConf() {
        try {
            println("trying to load....")
            PREFIX = Config.getConf("prefix", false)
            println(PREFIX)
            VerifyRole = Config.getConf("role", false)
            println(VerifyRole)
//            OptionalRole = Config.getConf("optionalrole", true)
//            println(OptionalRole)
            Endpoint = Config.getConf("apiendpoint", true)
            println(Endpoint)
            println("looks good...")
            assert(Endpoint != null)
            if (!Endpoint.equals("hypixel", ignoreCase = true) && !Endpoint.equals("slothpixel", ignoreCase = true)) {
                println("Invalid API endpoint, choose either Hypixel or Slothpixel. (Defaulting to Slothpixel)")
                Endpoint = "slothpixel"
            } else if (Endpoint.equals("hypixel", ignoreCase = true)) {
                APIKey = Config.getConf("hypixelapikey", true)
                if (APIKey == null) {
                    println("No API key given. Please enter one in the Config (Defaulting to Slothpixel)")
                    Endpoint = "slothpixel"
                }
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
                Guild = Config.getConf("guild", true)
                println(Guild)
            }
            if (Config.getConf("guildrole", true) != null) {
                GuildRole = Config.getConf("guildrole", true)
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