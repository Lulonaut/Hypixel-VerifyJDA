package de.lulonaut.Bot;

import de.lulonaut.Bot.commands.Calculate;
import de.lulonaut.Bot.commands.LinkDiscordHelp;
import de.lulonaut.Bot.commands.Verify;
import de.lulonaut.Bot.commands.config.ConfigCommand;
import de.lulonaut.Bot.commands.messagecount.DeleteAllMessages;
import de.lulonaut.Bot.commands.messagecount.LookupUser;
import de.lulonaut.Bot.commands.messagecount.MessageLeaderboard;
import de.lulonaut.Bot.listeners.CategoryCreateListener;
import de.lulonaut.Bot.listeners.MessageListener;
import de.lulonaut.Bot.utils.Conf;
import de.lulonaut.Bot.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {


    public static JDA jda;


    public static void main(String[] args) throws IOException, InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        Conf.loadConf();
        System.out.println("Prefix from Config set to: " + Conf.PREFIX);
        if (Conf.RankRoles) {
            System.out.println("You enabled rank roles. Please make sure the following roles exist: VIP, VIP+, MVP, MVP+, MVP++. Otherwise there will be constant Errors");
        }
        System.out.println("Config loaded.");
        try {
            //start bot with token
            jda = JDABuilder.createDefault(Config.getConf("token", false))
                    .setActivity(Activity.playing("https://github.com/Lulonaut/Hypixel-VerifyJDA"))
                    .build();
        } catch (LoginException e) {
            System.out.println("The Token is invalid! Please check your config.");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("There was an error while logging in, please try again and check your config!");
            e.printStackTrace();
            System.exit(1);
        }
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
        jda.addEventListener(new Verify());
        jda.addEventListener(new LinkDiscordHelp());
        jda.addEventListener(new MessageLeaderboard());
        jda.addEventListener(new LookupUser());
        jda.addEventListener(new DeleteAllMessages());
        jda.addEventListener(new ConfigCommand());
    }
}
