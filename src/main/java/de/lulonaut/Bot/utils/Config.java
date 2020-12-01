package de.lulonaut.Bot.utils;

import de.lulonaut.Bot.Main;
import de.lulonaut.Bot.errors.ConfigException;
import de.lulonaut.Bot.errors.ConfigNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * <h1>Config</h1>
 * This is a util Class that loads part of the Config on demand
 *
 * @see Main#loadConf()
 * @see Config#getConf(String, Boolean)
 */


public class Config {
    /**
     * <h1>Loads a part of the Config with the given String</h1>
     *
     * @param entry    (String) Entry in the file, eg: token
     * @param optional (Boolean) If the entry is not needed to run the program, set to true. Non existent
     * @return (String) Value of the config.
     * @throws IOException     On incorrect input
     * @throws ConfigException When optional is false but no entry is given or file doesn't exist
     */

    public static String getConf(String entry, Boolean optional) throws IOException, ConfigException, ConfigNotFoundException {
        String FilePath = "config/config.properties";
        String toReturn;

        Properties props = new Properties();
        File dir = new File(FilePath);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Created config file so it needs to be filled first!");
            throw new ConfigNotFoundException("Created a config file in " + FilePath + ". Please fill it like here: https://github.com/Lulonaut/Hypixel-VerifyJDA/blob/master/config/config.properties");
        }

        try {
            FileInputStream file = new FileInputStream(FilePath);
            props.load(file);
            file.close();
        } catch (FileNotFoundException e) {
            throw new ConfigException("config file not present.");
        }
        //Error Handling
        toReturn = props.getProperty(entry);
        if (toReturn == null && !optional) {
            throw new ConfigException(entry + " is missing in the Config file.");
        } else if (toReturn == null) {
            return null;
        } else if (toReturn.equals("") && !optional) {
            throw new ConfigException(entry + " can't be empty");
        }

        return props.getProperty(entry);
    }
}
