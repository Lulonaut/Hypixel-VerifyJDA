package de.lulonaut.Bot.utils;

import java.io.IOException;
import java.util.Arrays;

public class APITEST {
    public static void main(String[] args) throws IOException {
        String TestSubject = "Lulonaut";

        String[] result = API.getStuff(TestSubject);
        System.out.println(Arrays.toString(result));
    }
}
