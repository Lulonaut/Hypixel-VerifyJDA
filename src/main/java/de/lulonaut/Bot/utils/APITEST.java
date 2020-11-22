package de.lulonaut.Bot.utils;

import java.io.IOException;
import java.util.Arrays;

public class APITEST {
    public static void main(String[] args) throws IOException {
        String TestSubject = "Seselimo_";
        String Endpoint = "slothpixel";

        String[] result = API.getStuff(TestSubject, Endpoint);
        System.out.println(Arrays.toString(result));
    }
}
