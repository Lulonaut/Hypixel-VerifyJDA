package de.lulonaut.Bot.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Integer.parseInt;

class Storage {

    private static final File file = new File("test.json");

    public static void main(String[] args) throws IOException, ParseException {
//        HandleStuff("add", "myGuild", "myUser");
        Config(new String[]{"hello"}, "myGuild");
    }

    public static String[] HandleStuff(String Action, String GuildID, String UserID) throws IOException, ParseException {

        switch (Action) {
            case "add":
                messagesJSON(GuildID, UserID, true);
                break;
            case "remove":
                //remove one message
                messagesJSON(GuildID, UserID, false);
                break;
            case "top10":
                //Top10 for GuildID
                return sort(GuildID);
            case "lookup":
                //Check Message for UserID and GuildID
                return lookup(GuildID, UserID);
            default:
                throw new IllegalStateException("Unexpected value: " + Action);
        }
        return null;
    }

    public static void messagesJSON(String GuildID, String UserID, boolean addOrRemove) throws IOException, ParseException {

        //read current state
        FileReader reader = new FileReader(file);

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
        reader.close();
        JSONObject messages;
        try {
            try {
                messages = (JSONObject) jsonObject.get("messages");
            } catch (Exception e) {
                JSONObject messagesEmpty = new JSONObject();
                jsonObject.put("messages", messagesEmpty);
                messages = (JSONObject) jsonObject.get("messages");
            }

            if (messages == null) {
                JSONObject messagesEmpty = new JSONObject();
                jsonObject.put("messages", messagesEmpty);
                messages = (JSONObject) jsonObject.get("messages");
            }

            JSONObject guildMessages = (JSONObject) messages.get(GuildID);
            if (guildMessages == null) {
                JSONObject newGuild = new JSONObject();
                newGuild.put(UserID, 0);
                messages.put(GuildID, newGuild);

                PrintWriter printWriter = new PrintWriter("test.json");
                printWriter.write(String.valueOf(jsonObject));
                printWriter.flush();
                printWriter.close();

            } else {
                try {
                    String.valueOf(guildMessages.get(UserID));
                    parseInt(String.valueOf(guildMessages.get(UserID)));
                } catch (NullPointerException | NumberFormatException e) {
                    System.out.println("null pointer or null, setting to 0");
                    guildMessages.put(UserID, 0);
                }
                int currentmsg = parseInt(String.valueOf(guildMessages.get(UserID)));
                if (addOrRemove) {
                    currentmsg++;
                } else {
                    currentmsg--;
                }


                guildMessages.put(UserID, currentmsg);

                //write Changes to File
                PrintWriter printWriter = new PrintWriter("test.json");
                printWriter.write(String.valueOf(jsonObject));
                printWriter.flush();
                printWriter.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] sort(String GuildID) {
        //sorts the messages of a given GuildID by values
        List<User> list = new ArrayList<>();
        try {

            FileReader reader = new FileReader("test.json");

            String JsonFromFile = IOUtils.toString(reader);

            org.json.JSONObject jsonObject = new org.json.JSONObject(JsonFromFile);
            org.json.JSONObject messages = (org.json.JSONObject) jsonObject.get("messages");
            org.json.JSONObject jsonObj = (org.json.JSONObject) messages.get(GuildID);

            Iterator<?> keys = jsonObj.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                User user = new User(key, jsonObj.optInt(key));
                list.add(user);
            }

            list.sort((s1, s2) -> Integer.compare(s2.messages, s1.messages));

            String[] sortedUsers = {};
            for (User s : list) {

                sortedUsers = ArrayUtils.add(sortedUsers, s.UserID);
                sortedUsers = ArrayUtils.add(sortedUsers, String.valueOf(s.messages));
            }
            reader.close();
            return sortedUsers;

        } catch (JSONException e) {
            return new String[]{"0"};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] lookup(String GuildID, String UserID) throws IOException, ParseException {

        FileReader reader = new FileReader("test.json");

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
        reader.close();
        JSONObject messages;
        try {
            try {
                messages = (JSONObject) jsonObject.get("messages");
            } catch (Exception e) {
                JSONObject messagesEmpty = new JSONObject();
                jsonObject.put("messages", messagesEmpty);
                messages = (JSONObject) jsonObject.get("messages");
            }

            if (messages == null) {
                JSONObject messagesEmpty = new JSONObject();
                jsonObject.put("messages", messagesEmpty);
                messages = (JSONObject) jsonObject.get("messages");
            }

            JSONObject guildMessages = (JSONObject) messages.get(GuildID);
            if (guildMessages == null) {
                return new String[]{"0"};
            }

            return new String[]{String.valueOf(guildMessages.get(UserID))};

        } catch (JSONException | NullPointerException e) {
            return new String[]{"0"};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{"0"};
    }

    public static void Config(String[] ConfigStuff, String GuildID) throws IOException, ParseException {
        JSONObject newObject = new JSONObject();
        JSONObject newConfigObject = new JSONObject();
        JSONObject currentGuildConfigObject;
        //current State
        FileReader reader = new FileReader(file);
        JSONObject jso = (JSONObject) new JSONParser().parse(reader);
        reader.close();

        JSONObject configObject = (JSONObject) jso.get("config");

        //Create if it is not there
        if (configObject == null) {
            jso.put("config", newObject);
            configObject = (JSONObject) jso.get("config");
        }

        currentGuildConfigObject = (JSONObject) configObject.get(GuildID);
        if (currentGuildConfigObject == null) {
            configObject.put(GuildID, newConfigObject);
        }


//        write Changes
        FileWriter writer = new FileWriter("test.json");
        writer.write(String.valueOf(jso));
        writer.flush();
        writer.close();

    }
}


class User {
    String UserID;
    int messages;

    User(String username, int pwd) {
        this.UserID = username;
        this.messages = pwd;
    }
}
