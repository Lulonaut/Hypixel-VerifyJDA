package de.lulonaut.Bot.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.Integer.parseInt;

class Storage {

    public static void main(String[] args) throws IOException, ParseException {

        messagesJSON("thiswontwork", "1234657", true);
        messagesJSON("thiswontwork", "1234657", true);
        messagesJSON("thiswontwork", "1234657", true);
        messagesJSON("thiswontwork", "1234657", true);
        messagesJSON("thiswontwork", "1234657", true);
        messagesJSON("thiswontwork", "1234657", true);

    }

    public static void readJSON() {
        JSONParser jsonParser = new JSONParser();
        try {
            //Parsing the contents of the JSON file
            File file;
            FileReader reader = new FileReader("test.json");
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            //get name
            String name = (String) jsonObject.get("name");
            System.out.println(name);

            JSONObject messages = (JSONObject) jsonObject.get("messages");
//            System.out.println(messages);

            JSONObject firstGuild = (JSONObject) messages.get("myguildID");
//            System.out.println(firstGuild);
            System.out.println(firstGuild.get("myID"));
            reader.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void messagesJSON(String GuildID, String UserID, boolean addOrRemove) throws IOException, ParseException {
        //read current state
        JSONObject object = new JSONObject();
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
                } catch (NullPointerException e) {
                    System.out.println("null pointer, setting to 0");
                    guildMessages.put(UserID, 0);
                }

                String current = String.valueOf(guildMessages.get(UserID));
                int currentmsg = parseInt(current);
                currentmsg = currentmsg + 1;
                System.out.println("current:" + currentmsg);

                guildMessages.put(UserID, currentmsg);
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
}
