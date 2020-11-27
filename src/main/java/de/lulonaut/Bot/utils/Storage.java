package de.lulonaut.Bot.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


class Storage {

    public static void main(String[] args) throws IOException, ParseException {
        JSONObject object = new JSONObject();

        object.put("name", "test");
        object.put("test", true);

        PrintWriter printWriter = new PrintWriter("test.json");
        printWriter.write(object.toString());
        printWriter.flush();
        printWriter.close();
        readJSON();
    }

    public static void readJSON() {
        JSONParser jsonParser = new JSONParser();
        try {
            //Parsing the contents of the JSON file
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("test.json"));

            System.out.println(jsonObject.get("name"));

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

    }
}
