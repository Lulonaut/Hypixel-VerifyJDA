package de.lulonaut.bot.utils

import kotlin.Throws
import java.lang.Exception
import kotlin.jvm.JvmStatic
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.lang.NumberFormatException
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.io.IOUtils
import org.json.JSONException
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.*
import java.util.ArrayList

internal object Storage {
    private val file = File("test.json")
    @Throws(IOException::class, ParseException::class)
    @JvmStatic
    fun main(args: Array<String>) {
//        HandleStuff("add", "myGuild", "myUser");
        Config(arrayOf("hello"), "myGuild")
    }

    @Throws(IOException::class, ParseException::class)
    fun HandleStuff(Action: String, GuildID: String?, UserID: String?): Array<String>? {
        when (Action) {
            "add" -> messagesJSON(GuildID, UserID, true)
            "remove" ->                 //remove one message
                messagesJSON(GuildID, UserID, false)
            "top10" ->                 //Top10 for GuildID
                return sort(GuildID)
            "lookup" ->                 //Check Message for UserID and GuildID
                return lookup(GuildID, UserID)
            else -> throw IllegalStateException("Unexpected value: $Action")
        }
        return null
    }

    @Throws(IOException::class, ParseException::class)
    fun messagesJSON(GuildID: String?, UserID: String?, addOrRemove: Boolean) {

        //read current state
        val reader = FileReader(file)
        val jsonObject = JSONParser().parse(reader) as JSONObject
        reader.close()
        var messages: JSONObject?
        try {
            try {
                messages = jsonObject["messages"] as JSONObject?
            } catch (e: Exception) {
                val messagesEmpty = JSONObject()
                jsonObject["messages"] = messagesEmpty
                messages = jsonObject["messages"] as JSONObject?
            }
            if (messages == null) {
                val messagesEmpty = JSONObject()
                jsonObject["messages"] = messagesEmpty
                messages = jsonObject["messages"] as JSONObject?
            }
            val guildMessages = messages!![GuildID] as JSONObject?
            if (guildMessages == null) {
                val newGuild = JSONObject()
                newGuild[UserID] = 0
                messages[GuildID] = newGuild
                val printWriter = PrintWriter("test.json")
                printWriter.write(jsonObject.toString())
                printWriter.flush()
                printWriter.close()
            } else {
                try {
                    guildMessages[UserID].toString()
                    guildMessages[UserID].toString().toInt()
                } catch (e: NullPointerException) {
                    println("null pointer or null, setting to 0")
                    guildMessages[UserID] = 0
                } catch (e: NumberFormatException) {
                    println("null pointer or null, setting to 0")
                    guildMessages[UserID] = 0
                }
                var currentmsg = guildMessages[UserID].toString().toInt()
                if (addOrRemove) {
                    currentmsg++
                } else {
                    currentmsg--
                }
                guildMessages[UserID] = currentmsg

                //write Changes to File
                val printWriter = PrintWriter("test.json")
                printWriter.write(jsonObject.toString())
                printWriter.flush()
                printWriter.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sort(GuildID: String?): Array<String>? {
        //sorts the messages of a given GuildID by values
        val list: MutableList<User> = ArrayList()
        try {
            val reader = FileReader("test.json")
            val JsonFromFile = IOUtils.toString(reader)
            val jsonObject = org.json.JSONObject(JsonFromFile)
            val messages = jsonObject["messages"] as org.json.JSONObject
            val jsonObj = messages[GuildID] as org.json.JSONObject
            val keys: Iterator<*> = jsonObj.keys()
            while (keys.hasNext()) {
                val key = keys.next() as String
                val user = User(key, jsonObj.optInt(key))
                list.add(user)
            }
            list.sortWith(java.util.Comparator { s1: User, s2: User -> Integer.compare(s2.messages, s1.messages) })
            var sortedUsers: Array<String>? = arrayOf()
            for (s in list) {
                sortedUsers = ArrayUtils.add(sortedUsers, s.UserID)
                sortedUsers = ArrayUtils.add(sortedUsers, s.messages.toString())
            }
            reader.close()
            return sortedUsers
        } catch (e: JSONException) {
            return arrayOf("0")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class, ParseException::class)
    fun lookup(GuildID: String?, UserID: String?): Array<String> {
        val reader = FileReader("test.json")
        val jsonObject = JSONParser().parse(reader) as JSONObject
        reader.close()
        var messages: JSONObject?
        try {
            try {
                messages = jsonObject["messages"] as JSONObject?
            } catch (e: Exception) {
                val messagesEmpty = JSONObject()
                jsonObject["messages"] = messagesEmpty
                messages = jsonObject["messages"] as JSONObject?
            }
            if (messages == null) {
                val messagesEmpty = JSONObject()
                jsonObject["messages"] = messagesEmpty
                messages = jsonObject["messages"] as JSONObject?
            }
            val guildMessages = messages!![GuildID] as JSONObject? ?: return arrayOf("0")
            return arrayOf(guildMessages[UserID].toString())
        } catch (e: JSONException) {
            return arrayOf("0")
        } catch (e: NullPointerException) {
            return arrayOf("0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayOf("0")
    }

    @Throws(IOException::class, ParseException::class)
    fun Config(ConfigStuff: Array<String>?, GuildID: String?) {
        val newObject = JSONObject()
        val newConfigObject = JSONObject()
        val currentGuildConfigObject: JSONObject?
        //current State
        val reader = FileReader(file)
        val jso = JSONParser().parse(reader) as JSONObject
        reader.close()
        var configObject = jso["config"] as JSONObject?

        //Create config object if it does not exist
        if (configObject == null) {
            jso["config"] = newObject
            configObject = jso["config"] as JSONObject?
        }
        //Create subObject for guild config if it does not exist
        currentGuildConfigObject = configObject!![GuildID] as JSONObject?
        if (currentGuildConfigObject == null) {
            configObject[GuildID] = newConfigObject
        }


//        write Changes
        val writer = FileWriter("test.json")
        writer.write(jso.toString())
        writer.flush()
        writer.close()
    }
}

internal class User(var UserID: String, var messages: Int)