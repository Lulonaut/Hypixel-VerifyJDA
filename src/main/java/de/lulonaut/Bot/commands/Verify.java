package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.Main;
import de.lulonaut.Bot.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class Verify extends ListenerAdapter {
    /**
     * Verify Command
     */

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Checking if it's the actual command:
        String[] msg = event.getMessage().getContentRaw().split(" ");
        String[] APIResult;
        String UserDiscord = Objects.requireNonNull(event.getMember()).getUser().getAsTag();

        if (!msg[0].equalsIgnoreCase(Main.PREFIX + "verify")) {
            return;
        }
        //Checking correct usage
        if (msg.length != 2) {
            event.getChannel().sendMessage("Usage: " + Main.PREFIX + "verify [Minecraft IGN]").queue();
            return;
        }

        //Command logic
        try {
            //Getting the linked Discord + Error handling
            APIResult = API.getStuff(msg[1]);
            if (APIResult[0].equals("error")) {
                event.getChannel().sendMessage("There was an Error while checking your linked Discord, please try again later! (API probably down)").queue();
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage("Some error occurred, maybe the API is down. Please try again later").queue();
            return;
        }

        //Discord obtained, checking if it matches their Discord
        String Discord = APIResult[0];
        String Nickname = APIResult[1];
        String Rank = APIResult[2];
        String Guild = APIResult[3];

        //Case: Discord is null (not Linked anything)
        if (Discord.equals("null")) {
            //TODO add Command linkdc
            event.getChannel().sendMessage("Looks you didn't link a Discord yet. // If you don't know how to add one please type '" + Main.PREFIX + "linkdc'. If you just changed this please wait a few minutes and try again. (Spamming it won't do anything)").queue();
        }

        //Case : Discord doesn't match
        else if (!UserDiscord.equals(Discord)) {
            event.getChannel().sendMessage("Your Discord Tag is: `" + UserDiscord + "`. But a wise man told me you linked this Discord in Minecraft: `" + Discord + "`. If you just changed this please wait a few minutes and try again. (Spamming it won't do anything)").queue();
        }

        //Case : Discord does match
        else {
            //TODO: add additional logic (adding roles etc)
            event.getChannel().sendMessage("linked ig").queue();
        }


    }
}