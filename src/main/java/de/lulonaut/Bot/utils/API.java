package de.lulonaut.Bot.utils;

import de.lulonaut.Bot.Main;
import io.github.reflxction.hypixelapi.HypixelAPI;
import io.github.reflxction.hypixelapi.player.HypixelPlayer;
import io.github.reflxction.hypixelapi.player.SocialMediaType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class API {

    public static String[] getStuff(String name, String Endpoint) throws IOException {
        JSONObject guildAPI;
        JSONObject playerAPI;
        String Discord;
        String Nickname;
        String Rank;
        String Guild = null;
        if (Endpoint.equals("slothpixel")) {
            try {
                guildAPI = readJsonFromUrl("https://api.slothpixel.me/api/guilds/" + name);
                playerAPI = readJsonFromUrl("https://api.slothpixel.me/api/players/" + name);
            } catch (IOException e) {
                return new String[]{"error"};
            }

            //Error Handling
            if (playerAPI == null) {
                return new String[]{"error"};
            } else if (guildAPI == null) {
                return new String[]{"error"};
            }

            try {
                playerAPI.getString("error");
                guildAPI.getString("error");
                return new String[]{"error"};
            } catch (Exception ignored) {
            }

            //Get linked Discord
            JSONObject links = playerAPI.getJSONObject("links");
            Discord = links.getString("DISCORD");

            //get Nickname
            Nickname = playerAPI.getString("username");

            //get Rank
            Rank = playerAPI.getString("rank");

            //get Guild
            try {
                guildAPI.getString("guild");
            } catch (Exception e) {
                Guild = guildAPI.getString("name");
            }

            return new String[]{Discord, Nickname, Rank, Guild};
        } else if (Endpoint.equals("hypixel")) {
            HypixelAPI API = HypixelAPI.create(Main.APIKey);
            //get Discord
            HypixelPlayer player = API.getPlayer(name);
            Discord = player.getSocialMedia().getLinks().get(SocialMediaType.DISCORD);
            //set null to string
            Discord = (Discord == null) ? "null" : Discord;

            //get Nickname
            Nickname = player.getDisplayName();
            String UUID = String.valueOf(player.getUUID());

            //get Rank
            Rank = String.valueOf(player.getNewPackageRank());

            //get Guild
            String GuildID = API.getGuildId(Main.Guild);

            System.out.println(UUID);
            Guild = (API.getGuild(GuildID).getMembers().contains(UUID) ? Main.Guild : "null");
            System.out.println(API.getGuild(GuildID).getMembers());



            HypixelAPI.shutdown();
            return new String[]{Discord, Nickname, Rank, Guild};
        }
        return null;
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