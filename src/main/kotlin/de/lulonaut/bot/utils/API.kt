package de.lulonaut.bot.utils

import kotlin.Throws
import java.lang.Exception
import io.github.reflxction.hypixelapi.player.SocialMediaType
import io.github.reflxction.hypixelapi.HypixelAPI
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

object API {
    @Throws(IOException::class)
    fun getStuff(name: String, Endpoint: String?): Array<String?>? {
        val guildAPI: JSONObject?
        val playerAPI: JSONObject?
        var Discord: String
        val Nickname: String
        val Rank: String
        var Guild: String? = null
        return if (Endpoint == "slothpixel") {
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
            Discord = links.getString("DISCORD")

            //get Nickname
            Nickname = playerAPI.getString("username")

            //get Rank
            Rank = playerAPI.getString("rank")

            //get Guild
            try {
                guildAPI.getString("guild")
            } catch (e: Exception) {
                Guild = guildAPI.getString("name")
            }
            arrayOf(Discord, Nickname, Rank, Guild)
        } else if (Endpoint == "hypixel") {
            //establish API connection(s)
            val API = HypixelAPI.create(Conf.APIKey)
            val UUID =
                Objects.requireNonNull(readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/$name"))!!
                    .getString("id")
            val guild = readJsonFromUrl("https://api.hypixel.net/guild?key=" + Conf.APIKey + "&player=" + UUID)

            //get Discord
            val player = API.getPlayer(name)
            Discord = player.socialMedia.links[SocialMediaType.DISCORD].toString()
            //set null to string
            Discord = Discord ?: "null"

            //get Nickname
            Nickname = player.displayName

            //get Rank
            Rank = player.newPackageRank.toString()

            //get Guild
            println("UUID of $name : $UUID")
            Guild = try {
                assert(guild != null)
                guild!!.getJSONObject("guild").getString("name")
            } catch (e: Exception) {
                guild!!["guild"].toString()
            }
            arrayOf(Discord, Nickname, Rank, Guild)
        } else {
            null
        }
    }

    //util Functions for getting JSON (stolen from the Internet)
    @Throws(IOException::class)
    fun readAll(rd: Reader): String {
        val sb = StringBuilder()
        var cp: Int
        while (rd.read().also { cp = it } != -1) {
            sb.append(cp.toChar())
        }
        return sb.toString()
    }

    @Throws(IOException::class, JSONException::class)
    fun readJsonFromUrl(url: String?): JSONObject? {
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