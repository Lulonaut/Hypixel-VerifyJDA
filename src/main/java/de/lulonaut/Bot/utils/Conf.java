package de.lulonaut.Bot.utils;

import de.lulonaut.Bot.errors.ConfigException;
import de.lulonaut.Bot.errors.ConfigNotFoundException;

import java.io.IOException;
import java.util.Objects;

public class Conf {

    //All the variables loaded in the config
    public static String PREFIX;
    public static String VerifyRole;
    public static String OptionalRole;
    public static Boolean RankRoles = false;
    public static Boolean GuildRoles = false;
    public static String Guild = null;
    public static String GuildRole = null;
    public static String Endpoint; // API Endpoint
    public static String APIKey; //Api Key for Hypixel API

    /**
     * Util Function that loads all the Config values to the variables
     */

    public static void loadConf() throws IOException, AssertionError {
        try {
            PREFIX = Config.getConf("prefix", false);
            VerifyRole = Config.getConf("role", false);
            OptionalRole = Config.getConf("optionalrole", true);
            Endpoint = Config.getConf("apiendpoint", true);

            assert Endpoint != null;
            if (!Endpoint.equalsIgnoreCase("hypixel") && !Endpoint.equalsIgnoreCase("slothpixel")) {
                System.out.println("Invalid API endpoint, choose either Hypixel or Slothpixel. (Defaulting to Slothpixel)");
                Endpoint = "slothpixel";
            } else if (Endpoint.equalsIgnoreCase("hypixel")) {
                APIKey = Config.getConf("hypixelapikey", true);
                if (APIKey == null) {
                    System.out.println("No API key given. Please enter one in the Config (Defaulting to Slothpixel)");
                    Endpoint = "slothpixel";
                }
            }

            if (Objects.requireNonNull(Config.getConf("rankroles", true)).equalsIgnoreCase("true")) {
                RankRoles = true;
            }
            if (Objects.requireNonNull(Config.getConf("guildroletoggle", false)).equalsIgnoreCase("true")) {
                GuildRoles = true;
            }
            if (Config.getConf("guild", true) != null) {
                Guild = Config.getConf("guild", true);
            }
            if (Config.getConf("guildrole", true) != null) {
                GuildRole = Config.getConf("guildrole", true);
            }
        } catch (ConfigException | ConfigNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
