package de.lulonaut.Bot.errors;

import de.lulonaut.Bot.utils.Config;

/**
 * Fired when there was an Error while loading a part of the config.properties file
 *
 * @see Config
 */
public class ConfigException extends Exception {
    public ConfigException(String errorMessage) {
        super(errorMessage);
    }
}
