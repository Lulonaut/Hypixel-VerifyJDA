package de.lulonaut.Bot.errors;

/**
 * Fired when config/config.properties was not found.
 *
 * @see de.lulonaut.Bot.utils.Config
 */

public class ConfigNotFoundException extends Exception {
    public ConfigNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
