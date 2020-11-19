package de.lulonaut.Bot.listeners;

import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CategoryCreateListener extends ListenerAdapter {

    public void onCategoryCreate(CategoryCreateEvent event) {

        String name = event.getCategory().getName();
        event.getGuild().getDefaultChannel().sendMessage("Wow! Someone created a Category!!! The name is " + name).queue();

    }
}
