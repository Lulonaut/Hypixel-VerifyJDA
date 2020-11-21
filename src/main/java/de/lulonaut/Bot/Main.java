package de.lulonaut.Bot;

import de.lulonaut.Bot.commands.Calculate;
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
    public static String VerifyRole;
    public static String OptionalRole;
    public static Boolean RankRoles = false;
    public static Boolean GuildRoles = false;
    private static JDA jda;

    static {
        try {
            //start bot with token
            jda = JDABuilder.createDefault(Config.getConf("bot.token", false)).build();
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
        if (RankRoles) {
            System.out.println("You enabled rank roles. Please make sure the following roles exist: VIP, VIP+, MVP, MVP+, MVP++");
        }
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
        jda.addEventListener(new Verify());
    }

    public static void loadConf() throws IOException {
        PREFIX = Config.getConf("bot.prefix", false);
        VerifyRole = Config.getConf("bot.role", false);
        OptionalRole = Config.getConf("bot.optionalrole", true);
        if (Config.getConf("bot.rankroles", true).equalsIgnoreCase("true")) {
            RankRoles = true;
        }
        if(Config.getConf("bot.guildroletoggle", true).equalsIgnoreCase("true")){
            GuildRoles = true;
        }

    }

}
