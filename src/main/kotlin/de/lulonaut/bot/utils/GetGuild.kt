package de.lulonaut.bot.utils

import java.io.IOException
import java.lang.Exception
import de.lulonaut.wrapper.APIWrapper
import org.json.JSONException
import org.json.JSONObject

object GetGuild {
    fun getGuild(Username: String): String {
        return if (Conf.Endpoint == "slothpixel") {
            val gResponse: JSONObject?
            gResponse = try {
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
        } else if (Conf.Endpoint == "hypixel") {
            try {
                val API = APIWrapper.create(Conf.APIKey, true)
                val guild = API.getGuildByUsername(Username).name
                guild ?: ""
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
    }
}