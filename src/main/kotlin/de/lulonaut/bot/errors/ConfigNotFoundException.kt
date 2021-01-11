package de.lulonaut.bot.errors

import java.lang.Exception

/**
 * Fired when config/config.properties was not found.
 *
 * @see Config
 */
class ConfigNotFoundException(errorMessage: String?) : Exception(errorMessage)