package de.lulonaut.bot.utils

import de.lulonaut.wrapper.APIWrapper
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object Guild {
    fun getGuild(Username: String): String {
        return when (Conf.Endpoint) {
            "slothpixel" -> {
                val gResponse: JSONObject? = try {
                    API.readJsonFromUrl("https://api.slothpixel.me/api/guilds/$Username")
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
}