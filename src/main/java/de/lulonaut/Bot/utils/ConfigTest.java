package de.lulonaut.Bot.utils;

import de.lulonaut.Bot.errors.ConfigException;
import de.lulonaut.Bot.errors.ConfigNotFoundException;

import java.io.IOException;

public class ConfigTest {
    public static void main(String[] args) throws IOException, ConfigException, ConfigNotFoundException {

        System.out.println(Config.getConf("prefix", false));

    }
}
