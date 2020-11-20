package de.lulonaut.Bot.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class API {

    public static String getDiscord(String name) {
        //returns linked Discord
        try {
            JSONObject json = readJsonFromUrl("https://api.slothpixel.me/api/players/" + name);
            if (json == null) {
                return "404";
            }
            JSONObject links = json.getJSONObject("links");
            //return Discord link if there is no error
            try {
                return links.getString("DISCORD");
            } catch (JSONException e) {
                return "null";
            }
        } catch (Exception e) {
            //return "error" String when there is an error (Linked Discord can't be "error" so should be fine)
            return "error";
        }
    }
    public static String[] getError(String name){return new String[]{"error"};}
    public static String[] getStuff(String name) throws IOException {

        JSONObject player = readJsonFromUrl("https://api.slothpixel.me/api/players/" + name);
        String Discord = null;
        String Nickname = null;
        String Rank = null;

        JSONObject guild = readJsonFromUrl("https://api.slothpixel.me/api/guilds/" + name);
        String Guild = null;

        //Error Handling
        if (player == null) {
            return new String[]{"error"};
        } else if (guild == null) {
            return new String[]{"error"};
        }

        try {
            player.getString("error");
            guild.getString("error");
            return new String[]{"error"};
        } catch (Exception ignored) {
        }

        //Get linked Discord
        JSONObject links = player.getJSONObject("links");
        Discord = links.getString("DISCORD");

        //get Nickname
        Nickname = player.getString("username");

        //get Rank
        Rank = player.getString("rank");

        //get Guild
        try {
            guild.getString("guild");
        } catch (Exception e) {
            Guild = guild.getString("name");
        }

        return new String[]{Discord, Nickname, Rank, Guild};
    }

    //util Functions for getting JSON (stolen from the Internet)
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            is.close();
        }
    }


}