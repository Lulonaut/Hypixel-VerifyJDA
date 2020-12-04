package de.lulonaut.Bot.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Integer.parseInt;

class Storage {

    public static void main(String[] args) {
        String[] myList = sort("myGuildID");
        System.out.println(myList.length);
    }

    public static String[] HandleStuff(String Action, String GuildID, String UserID) {

        switch (Action) {
            case "add":
                //add one message
                break;
            case "remove":
                //remove one message
                break;
            case "top10":
                //Top10 for GuildID
                break;
            case "lookup":
                //Check Message for UserID and GuildID
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Action);
        }


        return new String[]{"returnSomething"};
    }

    public static int readJSON(String GuildID, String UserID) {
        JSONParser jsonParser = new JSONParser();
        try {
            //Parsing the contents of the JSON file
            FileReader reader = new FileReader("test.json");
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);


            JSONObject messages = (JSONObject) jsonObject.get("messages");
//            System.out.println(messages);

            JSONObject firstGuild = (JSONObject) messages.get(GuildID);
            reader.close();
            return Integer.parseInt(String.valueOf(firstGuild.get(UserID)));


        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void messagesJSON(String GuildID, String UserID, boolean addOrRemove) throws IOException, ParseException {
        //TODO remove or add one message

        //read current state
        FileReader reader = new FileReader("test.json");

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
        reader.close();
        try {
            JSONObject messages = (JSONObject) jsonObject.get("messages");
            JSONObject guildMessages = (JSONObject) messages.get(GuildID);
            if (guildMessages == null) {
                JSONObject newGuild = new JSONObject();
                newGuild.put(UserID, 0);
                messages.put(GuildID, newGuild);
                System.out.println(messages);

                PrintWriter printWriter = new PrintWriter("test.json");
                printWriter.write(String.valueOf(jsonObject));
                printWriter.flush();
                printWriter.close();

            } else {
                System.out.println("else");
                try {
                    String current = String.valueOf(guildMessages.get(UserID));
                    int currentmsg = parseInt(String.valueOf(guildMessages.get(UserID)));
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

                System.out.println("current:" + currentmsg);

                guildMessages.put(UserID, currentmsg);

                //write Changes to File
                PrintWriter printWriter = new PrintWriter("test.json");
                printWriter.write(String.valueOf(jsonObject));
                printWriter.flush();
                printWriter.close();
            }

            System.out.println(guildMessages);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONArray Jsonarray(String GuildID) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();

        FileReader reader = new FileReader("test.json");
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        JSONObject messages = (JSONObject) jsonObject.get("messages");
        JSONObject guild = (JSONObject) messages.get(GuildID);

        JSONArray array = new JSONArray();
        array.add(guild);
        reader.close();
        return array;

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

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
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
