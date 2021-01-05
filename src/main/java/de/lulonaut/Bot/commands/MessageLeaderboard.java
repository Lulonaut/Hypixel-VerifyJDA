package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.utils.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageLeaderboard extends ListenerAdapter {
    List<String> aliases = Stream.of(Aliases.LeaderboardAliases.values())
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


        LinkedList<String> users = new LinkedList<>();
        LinkedList<Integer> messages = new LinkedList<>();

        Map<String, Integer> leaderboard = Database.sort(event.getGuild().getId());
        for (Map.Entry<String, Integer> current : leaderboard.entrySet()) {
            users.add(current.getKey());
            messages.add(current.getValue());
        }

        if (users.size() > 10) {
            users.subList(9, users.size());
        }

        StringBuilder lb = new StringBuilder();

        for (int i = 0; i < users.size(); i++) {
            lb.append("<@!");
            lb.append(users.get(i));
            lb.append(">");
            lb.append(" has ");
            lb.append(messages.get(i));
            lb.append(" messages and is Place ");
            lb.append(i + 1);
            lb.append("\n");
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Current Leaderboard for " + event.getGuild().getName(), null)
                .setDescription(lb.toString());

        event.getChannel().sendMessage(eb.build()).queue();

    }
}
