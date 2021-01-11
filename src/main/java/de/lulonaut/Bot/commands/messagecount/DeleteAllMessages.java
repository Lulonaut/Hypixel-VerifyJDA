package de.lulonaut.Bot.commands.messagecount;

import de.lulonaut.Bot.commands.Aliases;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeleteAllMessages extends ListenerAdapter {
    List<String> aliases = Stream.of(Aliases.DeleteMessagesAliases.values())
            .map(Enum::name)
            .collect(Collectors.toList());


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!aliases.contains(msg[0].substring(1))) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        EnumSet<Permission> perms = Objects.requireNonNull(event.getMember()).getPermissions();
        if (!perms.contains(Permission.MANAGE_SERVER)) {
            eb.setTitle("Error!");
            eb.setDescription("");
            event.getChannel().sendMessage(eb.build()).queue();
            return;
        }
        //user has perms
        event.getChannel().sendMessage("Please type \"confirm\" to reset the counter for this server. **THIS CANNOT BE UNDONE**").queue();
        DeletingStateMachine dsm = new DeletingStateMachine(event.getChannel(), event.getMember().getUser());
        event.getJDA().addEventListener(dsm);
    }
}
