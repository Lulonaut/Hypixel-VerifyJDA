package de.lulonaut.Bot.commands.messagecount;

import de.lulonaut.Bot.utils.Database;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DeletingStateMachine extends ListenerAdapter {
    private final long channelID, userID;
    private int usages;

    public DeletingStateMachine(MessageChannel channel, User author) {
        this.channelID = channel.getIdLong();
        this.userID = author.getIdLong();
        this.usages = 0;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getChannel().getIdLong() != channelID) {
            return;
        }
        if (event.getAuthor().getIdLong() != userID) {
            return;
        }

        //actual logic
        if (!event.getMessage().getContentRaw().equalsIgnoreCase("confirm")) {
            usages = usages + 1;
            if (usages == 1) {
                return;
            }
            event.getChannel().sendMessage("Not resetting.").queue();
        } else {
            Database.removeAllMessages(event.getGuild().getId());
            event.getChannel().sendMessage("Counter reset.").queue();
        }
        event.getJDA().removeEventListener(this);

    }

}
