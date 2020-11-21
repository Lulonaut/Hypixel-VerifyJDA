package de.lulonaut.Bot.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class API {

    public static String[] getStuff(String name) {
        JSONObject guild;
        JSONObject player;

        try {
            guild = readJsonFromUrl("https://api.slothpixel.me/api/guilds/" + name);
            player = readJsonFromUrl("https://api.slothpixel.me/api/players/" + name);
        } catch (IOException e) {
            return new String[]{"error"};
        }

        String Discord;
        String Nickname;
        String Rank;


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
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            rd.close();
            return new JSONObject(jsonText);
        } catch (FileNotFoundException e) {
            return null;
        }
    }


}