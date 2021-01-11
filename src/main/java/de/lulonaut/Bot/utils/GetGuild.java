package de.lulonaut.Bot.utils;

import de.lulonaut.wrapper.APIWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GetGuild {

    public static String getGuild(String Username) {
        if (Conf.Endpoint.equals("slothpixel")) {
            JSONObject gResponse;
            try {
                gResponse = API.readJsonFromUrl("https://api.slothpixel.me/api/guilds/" + Username);
            } catch (IOException e) {
                return "";
            }
            if (gResponse != null && gResponse.has("guild")) {
                return "";
            }

            try {
                return gResponse != null ? gResponse.getString("name") : "";
            } catch (JSONException e) {
                return "";
            }
        } else if (Conf.Endpoint.equals("hypixel")) {
            try {
                de.lulonaut.wrapper.core.API API = APIWrapper.create(Conf.APIKey, true);
                String guild = API.getGuildByUsername(Username).getName();
                if (guild == null) {
                    return "";
                } else {
                    return guild;
                }
            } catch (Exception e) {
                return "";

            }
        } else {
            return "";
        }
    }
}
