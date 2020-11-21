package de.lulonaut.Bot.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Config {

    public static Properties getProp() throws IOException {
        Properties props = new Properties();
        FileInputStream file = new FileInputStream("config/config.properties");
        props.load(file);
        file.close();
        return props;

    }

    public static String getConf(String entry, Boolean optional) throws IOException {
        Properties prop = getProp();
        String toReturn;

        toReturn = prop.getProperty(entry);
        if (toReturn == null && !optional) {
            System.out.println("Some part of the config is missing: " + entry + "\nPlease add it or redownload the config file!");
            System.exit(1);
        } else if (toReturn == null){
            return null;
        }
        return prop.getProperty(entry);
    }
}