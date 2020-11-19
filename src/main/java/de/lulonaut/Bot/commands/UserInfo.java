package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserInfo extends ListenerAdapter {


    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {


        if (!event.getMessage().getContentRaw().equalsIgnoreCase(Main.PREFIX + "userinfo")) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("UserInfo");
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
