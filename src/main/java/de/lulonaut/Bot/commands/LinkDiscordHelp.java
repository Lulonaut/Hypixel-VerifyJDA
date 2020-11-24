package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.Main;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class LinkDiscordHelp extends ListenerAdapter {
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentRaw().split(" ");

        if(msg[0].equalsIgnoreCase(Main.PREFIX + "linkdc")){
            event.getChannel().sendMessage("1.Go to /lobby on Hypixel\n2.Click on your Head (second Slot)\n3.Click on the twitter symbol (column 3, row 4)\n4.Click on the Discord Symbol (second to last one) and paste your Discord Link in the Chat\nhttps://gfycat.com/dentaltemptingleonberger").queue();
        }
    }
}
