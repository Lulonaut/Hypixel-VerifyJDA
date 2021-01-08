package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.Main;
import de.lulonaut.Bot.utils.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LookupUser extends ListenerAdapter {
    List<String> aliases = Stream.of(Aliases.LookupUserAliases.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentRaw().split(" ");

        try {
            if (!aliases.contains(msg[0].substring(1))) {
                return;
            }
        } catch (Exception e) {
            return;
        }


        if (msg.length != 2) {
            event.getChannel().sendMessage(event.getAuthor().getName() + " currently has " + Database.lookupUser(event.getGuild().getId(), event.getAuthor().getId()) + " messages in this server!").queue();
        } else {
            EmbedBuilder eb = new EmbedBuilder();
            String UserID = msg[1].replaceAll("[^0-9]", "");
            if (UserID.equals("")) {
                eb.setTitle("Error!");
                eb.setDescription("That user doesnt exist! Please @ them or use their UserID as the parameter.");
                event.getChannel().sendMessage(eb.build()).queue();
                return;
            }

            try {
                Objects.requireNonNull(Main.jda.getUserById(UserID)).getName();
            } catch (NullPointerException e) {
                eb.setTitle("Error!");
                eb.setDescription("That user doesnt exist! Please @ them or use their UserID as the parameter.");
                event.getChannel().sendMessage(eb.build()).queue();
                return;
            }
            eb.setTitle("Success");
            eb.setDescription("<@!" + UserID + "> currently has " + Database.lookupUser(event.getGuild().getId(), UserID) + " messages in this server");
            event.getChannel().sendMessage(eb.build()).queue();
        }

    }
}