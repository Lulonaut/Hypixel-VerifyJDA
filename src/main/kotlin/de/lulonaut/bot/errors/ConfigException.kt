package de.lulonaut.bot.errors

import java.lang.Exception

/**
 * Fired when there was an Error while loading a part of the config.properties file
 *
 * @see Config
 */
class ConfigException(errorMessage: String?) : Exception(errorMessage)