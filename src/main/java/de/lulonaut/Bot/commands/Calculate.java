package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.Main;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class Calculate extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String[] msg = event.getMessage().getContentRaw().split(" ");


        if (msg[0].equalsIgnoreCase(Main.PREFIX + "calculate")) {
            if (msg.length != 4) {
                event.getChannel().sendMessage("Missing Parameters! Usage: " + Main.PREFIX + " [add/sub] [first-num] [second-num]").queue();
                return;
            }

            float num1 = Integer.parseInt(msg[2]);
            float num2 = Integer.parseInt(msg[3]);
            float equals;

            switch (msg[1]) {
                case "add":
                    equals = num1 + num2;

                    event.getChannel().sendMessage(num1 + " + " + num2 + " equals: " + equals).queue();
                    return;

                case "sub":

                    equals = num1 - num2;

                    event.getChannel().sendMessage(num1 + " - " + num2 + " equals: " + equals).queue();

                    return;

                default:
                    event.getChannel().sendMessage("Wrong Parameters! Usage: .calculate [add/sub] [first-num] [second-num").queue();
            }
        }
    }
}