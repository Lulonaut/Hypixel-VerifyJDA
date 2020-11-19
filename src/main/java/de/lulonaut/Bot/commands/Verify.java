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
        String DiscordLinked;
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
            DiscordLinked = API.getDiscord(msg[1]);
            if (DiscordLinked.equals("error")) {
                event.getChannel().sendMessage("There was an Error while checking your linked Discord, please try again later! (API probably down)").queue();
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage("Some error occured, maybe the API is down. Please try again later").queue();
            return;
        }

        //Discord optained, checking if it matches their Discord
        //Case: Discord is null (not Linked anything)

        if (DiscordLinked.equals("null")) {
            //TODO add Command
            event.getChannel().sendMessage("Looks you didn't link a Discord yet. // If you don't know how to add one please type '" + Main.PREFIX + "linkdc'. If you just changed this please wait a few minutes and try again. (Spamming it won't do anything)").queue();
            return;
        }

        //Case : Discord doesn't match
        if (!UserDiscord.equals(DiscordLinked)) {
            event.getChannel().sendMessage("Your Discord Tag is: `" + UserDiscord + "`. But the API returned the following for your linked Discord: `" + DiscordLinked + "`. If you just changed this please wait a few minutes and try again. (Spamming it won't do anything)").queue();
        }

        //TODO: add additonal logic

    }
}