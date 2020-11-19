package de.lulonaut.Bot.utils;

public class APITEST {
    public static void main(String[] args) {
        String TestSubject = "Lulonaut";

        String result = API.getDiscord(TestSubject);
        System.out.println(result);
    }
}
