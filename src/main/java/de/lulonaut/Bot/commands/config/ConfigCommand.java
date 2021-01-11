package de.lulonaut.Bot.commands.config;

import de.lulonaut.Bot.utils.Conf;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class ConfigCommand extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.getMessage().getContentRaw().equalsIgnoreCase(Conf.PREFIX + "config")) {
            return;
        } else {
            if (!Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.MANAGE_SERVER)) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Error!");
                eb.setDescription("To use this command you need to have the \"Manage Server\" permission!");
                event.getChannel().sendMessage(eb.build()).queue();
                return;
            }

            System.out.println("starting state machine");
            ConfigStateMachine csm = new ConfigStateMachine(event.getChannel(), event, event.getMember());
            event.getJDA().addEventListener(csm);
            new Thread(() -> {
                try {
                    //timeout of 5 minutes
                    System.out.println("going to sleep");
                    Thread.sleep(300000);
                    System.out.println("woke up");
                    System.out.println(csm.done);
                    if (!csm.done) {
                        System.out.println("not done, cancelling");
                        event.getChannel().sendMessage("Timeout reached, canceling config.").queue();
                        event.getJDA().removeEventListener(csm);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
