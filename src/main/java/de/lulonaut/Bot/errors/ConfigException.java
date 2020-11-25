package de.lulonaut.Bot.errors;

/**
 * Fired when there was an Error while loading a part of the config.properties file
 *
 * @see de.lulonaut.Bot.utils.Config
 */
public class ConfigException extends Exception {
    public ConfigException(String errorMessage) {
        super(errorMessage);
    }
}
