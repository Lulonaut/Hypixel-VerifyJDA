package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.utils.Conf;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class LinkDiscordHelp extends ListenerAdapter {
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentRaw().split(" ");

        if (msg[0].equalsIgnoreCase(Conf.PREFIX + "linkdc")) {
            event.getChannel().sendMessage(
                    "1.Go to /lobby on Hypixel\n" +
                            "2.Click on your Head (second Slot)\n" +
                            "3.Click on the twitter symbol (column 3, row 4)\n" +
                            "4.Click on the Discord Symbol (second to last one) and paste your Discord Link in the Chat\n" +
                            "https://gfycat.com/dentaltemptingleonberger (Stolen video)").queue();
        }
    }
}
