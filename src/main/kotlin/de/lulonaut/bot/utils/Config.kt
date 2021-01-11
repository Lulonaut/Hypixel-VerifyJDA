package de.lulonaut.bot.utils

import kotlin.Throws
import de.lulonaut.bot.errors.ConfigException
import de.lulonaut.bot.errors.ConfigNotFoundException
import java.io.*
import java.util.*

/**
 * <h1>Config</h1>
 * This is a util Class that loads part of the Config on demand
 *
 * @see Conf
 *
 * @see Config.getConf
 */
object Config {
    /**
     * <h1>Loads a part of the Config with the given String</h1>
     *
     * @param entry    (String) Entry in the file, eg: token
     * @param optional (Boolean) If the entry is not needed to run the program, set to true.
     * Non existent values will be ignored when true
     * @return (String) Value of the config.
     * @throws IOException     On incorrect input
     * @throws ConfigException When optional is false but no entry is given or file doesn't exist
     */
    @Throws(IOException::class, ConfigException::class, ConfigNotFoundException::class)
    fun getConf(entry: String, optional: Boolean?): String? {
        val filePath = "config/config.properties"
        val toReturn: String
        val props = Properties()
        val dir = File(filePath)
        if (!dir.exists()) {
            dir.mkdirs()
            println("Created config file so it needs to be filled first!")
            throw ConfigNotFoundException("Created a config file in $filePath. Please fill it like here: https://github.com/Lulonaut/Hypixel-VerifyJDA/blob/master/config/config.properties")
        }
        try {
            val file = FileInputStream(filePath)
            props.load(file)
            file.close()
        } catch (e: FileNotFoundException) {
            throw ConfigException("config file not present.")
        }
        //Error Handling
        toReturn = props.getProperty(entry)
        if (toReturn == null && !optional!!) {
            throw ConfigException("$entry is missing in the Config file.")
        } else if (toReturn == null) {
            return null
        } else if (toReturn == "" && !optional!!) {
            throw ConfigException("$entry can't be empty")
        }
        return props.getProperty(entry)
    }
}