package de.lulonaut.Bot;

import de.lulonaut.Bot.commands.Calculate;
import de.lulonaut.Bot.commands.UserInfo;
import de.lulonaut.Bot.commands.Verify;
import de.lulonaut.Bot.listeners.CategoryCreateListener;
import de.lulonaut.Bot.listeners.MessageListener;
import de.lulonaut.Bot.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static String PREFIX;
    private static JDA jda;

    static {
        try {
            jda = JDABuilder.createDefault(Config.getConf("bot.token")).build();
        } catch (LoginException e) {
            System.out.println("The Token is invalid! Please check your config.");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("There was an error while logging in, please try again and check your config!");
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        loadConf();
        System.out.println("Prefix from Config set to: " + PREFIX);
        System.out.println("Config loaded.");
        registerEvents();
        System.out.println("All Events registered!");
        registerCommands();
        System.out.println("All Commands registered!");
    }

    public static void registerEvents() {
        jda.addEventListener(new MessageListener());
        jda.addEventListener(new CategoryCreateListener());

    }

    public static void registerCommands() {
        jda.addEventListener(new Calculate());
        jda.addEventListener(new UserInfo());
        jda.addEventListener(new Verify());
    }

    public static void loadConf() throws IOException {
        PREFIX = Config.getConf("bot.prefix");
    }

}
