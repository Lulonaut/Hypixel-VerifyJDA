package de.lulonaut.bot.utils

import de.lulonaut.wrapper.APIWrapper
import de.lulonaut.wrapper.core.API
import de.lulonaut.wrapper.core.player.Player
import io.github.reflxction.hypixelapi.HypixelAPI
import io.github.reflxction.hypixelapi.player.SocialMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

object API {
    @Throws(IOException::class)
    fun getStuff(name: String, Endpoint: String?): Array<String?>? {
        val guildAPI: JSONObject?
        val playerAPI: JSONObject?
        val discord: String
        val nickname: String
        val rank: String
        var guild: String? = null
        return when (Endpoint) {
            "slothpixel" -> {
                try {
                    guildAPI = readJsonFromUrl("https://api.slothpixel.me/api/guilds/$name")
                    playerAPI = readJsonFromUrl("https://api.slothpixel.me/api/players/$name")
                } catch (e: IOException) {
                    return arrayOf("error")
                }

                //Error Handling
                if (playerAPI == null) {
                    return arrayOf("error")
                } else if (guildAPI == null) {
                    return arrayOf("error")
                }
                try {
                    playerAPI.getString("error")
                    guildAPI.getString("error")
                    return arrayOf("error")
                } catch (ignored: Exception) {
                }

                //Get linked Discord
                val links = playerAPI.getJSONObject("links")
                discord = links.getString("DISCORD")

                //get Nickname
                nickname = playerAPI.getString("username")

                //get Rank
                rank = playerAPI.getString("rank")

                //get guild
                try {
                    guildAPI.getString("guild")
                } catch (e: Exception) {
                    guild = guildAPI.getString("name")
                }
                arrayOf(discord, nickname, rank, guild)
            }
            "hypixel2" -> {
                //establish API connection(s)
                val api = HypixelAPI.create(Conf.APIKey)
                val uuid =
                    Objects.requireNonNull(readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/$name"))!!
                        .getString("id")
                val guildObject =
                    readJsonFromUrl("https://api.hypixel.net/guild?key=" + Conf.APIKey + "&player=" + uuid)

                //get Discord
                val player = api.getPlayer(name)
                discord = player.socialMedia.links[SocialMediaType.DISCORD].toString()
                //set null to string

                //get Nickname
                nickname = player.displayName

                //get Rank
                rank = player.newPackageRank.toString()

                //get Guild
                println("UUID of $name : $uuid")
                guild = try {
                    assert(guildObject != null)
                    guildObject!!.getJSONObject("guild").getString("name")
                } catch (e: Exception) {
                    guildObject!!["guild"].toString()
                }
                arrayOf(discord, nickname, rank, guild)
            }
            "hypixel" -> {
                val api: API = APIWrapper.create(Conf.APIKey, true)
                val player: Player = api.getPlayerByUsername(name)
                discord = player.socialMedia.discord
                nickname = player.displayName
                rank = player.currentRank
                guild = api.getGuildByUsername("Lulonaut").name
                arrayOf(discord, nickname, rank, guild)
            }
            else -> {
                null
            }
        }
    }

    fun getGuild(Username: String): String {
        return when (Conf.Endpoint) {
            "slothpixel" -> {
                val gResponse: JSONObject? = try {
                    readJsonFromUrl("https://api.slothpixel.me/api/guilds/$Username")
                } catch (e: IOException) {
                    return ""
                }
                if (gResponse != null && gResponse.has("guild")) {
                    return ""
                }
                try {
                    if (gResponse != null) gResponse.getString("name") else ""
                } catch (e: JSONException) {
                    ""
                }
            }
            "hypixel" -> {
                try {
                    val api = APIWrapper.create(Conf.APIKey, true)
                    val guild = api.getGuildByUsername(Username).name
                    guild ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            }
            else -> ""
        }
    }

    //util Functions for getting JSON (stolen from the Internet)
    @Throws(IOException::class)
    private fun readAll(rd: Reader): String {
        val sb = StringBuilder()
        var cp: Int
        while (rd.read().also { cp = it } != -1) {
            sb.append(cp.toChar())
        }
        return sb.toString()
    }

    @Throws(IOException::class, JSONException::class)
    private fun readJsonFromUrl(url: String?): JSONObject? {
        try {
            URL(url).openStream().use { `is` ->
                val rd = BufferedReader(InputStreamReader(`is`, StandardCharsets.UTF_8))
                val jsonText = readAll(rd)
                rd.close()
                return JSONObject(jsonText)
            }
        } catch (e: FileNotFoundException) {
            return null
        }
    }
}