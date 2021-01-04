package de.lulonaut.Bot.listeners;

import de.lulonaut.Bot.utils.Database;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class MessageListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (Objects.requireNonNull(event.getMember()).getUser().isBot()) {
            return;
        }
        Database.addMessage(event.getGuild().getId(), event.getMember().getUser().getId());
    }

}
