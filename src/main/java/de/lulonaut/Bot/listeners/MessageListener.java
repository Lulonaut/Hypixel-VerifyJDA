package de.lulonaut.Bot.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class MessageListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (Objects.requireNonNull(event.getMember()).getUser().isBot()) {
            return;
        }

        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (msg[0].equalsIgnoreCase("hi") || msg[0].equalsIgnoreCase("hi,")) {

            String name;
            if (event.getMember().getNickname() == null) {
                name = event.getMember().getUser().getName();
            } else {
                name = event.getMember().getNickname();
            }
            event.getChannel().sendMessage("Hi, " + name).queue();
        }
    }
}
