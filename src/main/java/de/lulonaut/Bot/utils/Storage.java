package de.lulonaut.Bot.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

class Storage {

    public static void main(String[] args) throws IOException, ParseException {
        /*JSONObject object = new JSONObject();
        object.put("name", "test");
        object.put("test", true);

        PrintWriter printWriter = new PrintWriter("test.json");
        printWriter.write(object.toString());
        printWriter.flush();
        printWriter.close();*/
        readJSON();
    }

    public static void readJSON() {
        JSONParser jsonParser = new JSONParser();
        try {
            //Parsing the contents of the JSON file
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("test.json"));

            //get name
            String name = (String) jsonObject.get("name");
            System.out.println(name);

            JSONObject messages = (JSONObject) jsonObject.get("messages");
//            System.out.println(messages);

            JSONObject firstGuild = (JSONObject) messages.get("myguildID");
//            System.out.println(firstGuild);
            System.out.println(firstGuild.get("myID"));

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

    }
}
