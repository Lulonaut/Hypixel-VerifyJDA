package de.lulonaut.Bot.errors;

import de.lulonaut.Bot.utils.Config;

/**
 * Fired when config/config.properties was not found.
 *
 * @see Config
 */

public class ConfigNotFoundException extends Exception {
    public ConfigNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
