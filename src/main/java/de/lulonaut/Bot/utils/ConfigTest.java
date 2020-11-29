package de.lulonaut.Bot.utils;

import de.lulonaut.Bot.errors.ConfigException;

import java.io.IOException;

public class ConfigTest {
    public static void main(String[] args) throws IOException, ConfigException {

        System.out.println(Config.getConf("prefix", false));

    }
}
