package de.lulonaut.Bot.commands.config;

import de.lulonaut.Bot.utils.Cache;
import de.lulonaut.Bot.utils.Conf;
import de.lulonaut.Bot.utils.Database;
import de.lulonaut.Bot.utils.GetGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ConfigStateMachine extends ListenerAdapter {
    private final long channelID, userID;

    private final Map<String, String> options = new HashMap<>();
    public boolean done;
    private GuildMessageReceivedEvent initEvent;
    private int currentStep;
    private boolean inProgress;
    /*
    Steps:
    1: Verified role
    2: message counting system toggle
    3: guild member role toggle
    4: if 3: guild name (validated with MC name)
     */

    public ConfigStateMachine(MessageChannel channel, GuildMessageReceivedEvent event, Member member) {
        System.out.println("starting");
//        this.guildID = guild.getIdLong();
        this.channelID = channel.getIdLong();
        this.userID = member.getIdLong();
        this.currentStep = 1;
        this.done = false;
        this.initEvent = event;
        //default options
        options.put("role", "Hypixel Verified");
        options.put("counting", "false");
        options.put("guildMemberRole", "false");
        options.put("guildID", "");
    }

    private String showCurrentConfig() {
        StringBuilder sb = new StringBuilder();
        String count;
        if (options.get("counting").equals("true")) {
            count = "enabled";
        } else {
            count = "disabled";
        }
        String gRole;
        boolean gmRole;
        if (options.get("guildMemberRole").equals("true")) {
            gRole = "enabled";
            gmRole = true;
        } else {
            gRole = "disabled";
            gmRole = false;
        }
        sb.append("Verified Role: `").append(options.get("role")).append("`\n");
        sb.append("Message counting system: `").append(count).append("`\n");
        sb.append("Guild member role: `").append(gRole).append("`\n");
        if (gmRole) {
            sb.append("Guild Name: `").append(options.get("guildID")).append("`");
        }

        return sb.toString();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("event...");
        if (event.getAuthor().isBot() || event.getChannel().getIdLong() != channelID || event.getAuthor().getIdLong() != userID) {
            System.out.println("return1");
            return;
        }

        if ((event.getMessage().getContentRaw().equalsIgnoreCase("cancel") && !inProgress)) {
            event.getChannel().sendMessage("setup cancelled.").queue();
            event.getJDA().removeEventListener(this);
            System.out.println("return2");
            return;
        }
        if ((event.getMessage().getContentRaw().equalsIgnoreCase("continue") && !inProgress)) {
            System.out.println("continue");
            currentStep++;
        }
        if ((event.getMessage().getContentRaw().equalsIgnoreCase("exit") && !inProgress)) {
            System.out.println(options);
            Database.saveConfig(event.getGuild().getId(), options);
            //TODO: Write changes to Database (new method for saving and getting current config)
            event.getChannel().sendMessage("Config saved to Database.\nCurrent config:" +
                    showCurrentConfig()).queue();
            event.getJDA().removeEventListener(this);
            System.out.println("return3");
            return;
        }
        System.out.println("switch with " + currentStep);
        switch (currentStep) {
            case 1:
                //verify role
                if (inProgress) {
                    options.put("role", event.getMessage().getContentRaw());
                    event.getChannel().sendMessage("OK: Verify Role is now set to: `" + event.getMessage().getContentRaw() + "` Please make sure this role exists before proceeding\nType `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.").queue();
                    inProgress = false;
                } else {
                    event.getChannel().sendMessage("Welcome to the config setup\nPlease specify a Role users get when verifying successfully. This is currently set to `" + options.get("role") + "`").queue();
                    inProgress = true;
                }
                break;
            case 2:
                //message counting
                if (inProgress) {
                    if (event.getMessage().getContentRaw().equalsIgnoreCase("enabled")) {
                        options.put("counting", "true");
                        event.getChannel().sendMessage("OK: Message counting system is now enabled.\n" +
                                "With it come some commands:\n" +
                                Conf.PREFIX + "`leaderboard`: shows the current leaderboard of users with the most messages\n" +
                                Conf.PREFIX + "`check <user>`: shows how many messages you or a given user currently have\n" +
                                Conf.PREFIX + "`deleteMessages`: this command requires the \"Manage Server\" Permission and clears the current message counter for everyone in the server. (further confirmation is required)\n" +
                                "\n" +
                                "Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.").queue();
                        inProgress = false;
                    } else if (event.getMessage().getContentRaw().equalsIgnoreCase("disabled")) {
                        options.put("counting", "false");
                        event.getChannel().sendMessage("OK: Message counting system is now disabled.\n" +
                                "Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.").queue();
                        inProgress = false;
                    } else {
                        event.getChannel().sendMessage("Invalid input, please type `enabled` or `disabled`.").queue();
                    }
                } else {
                    event.getChannel().sendMessage("The message counting system tracks every message from every user and allows for a leaderboard, individual checking for a user and resetting of the counter.\n" +
                            "type one of the following: `enabled` to enable it or `disabled` to disable it.").queue();
                    inProgress = true;
                }
                break;
            case 3:
                //guild member role toggle
                if (inProgress) {
                    if (event.getMessage().getContentRaw().equalsIgnoreCase("enabled")) {
                        options.put("guildMemberRole", "true");
                        event.getChannel().sendMessage("OK: guild member role enabled. In the next step you will have to set a guild name. If you skip this step the feature will not work.\n" +
                                "Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.").queue();
                        inProgress = false;
                        return;
                    } else if (event.getMessage().getContentRaw().equalsIgnoreCase("disabled")) {
                        options.put("guildMemberRole", "false");
                        event.getChannel().sendMessage("OK: guild member role disabled.\n" +
                                "Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.").queue();
                        currentStep++;
                        inProgress = false;
                        return;
                    } else {
                        event.getChannel().sendMessage("Invalid input, please type `enabled` or `disabled`.").queue();
                    }
                } else {
                    event.getChannel().sendMessage("Setting up a guild role allows you to skip giving each guild member a role by hand if you run a Discord for a guild. If you don't this feature is pretty useless.\n" +
                            "Type one of the following: `enabled` to enable it or `disabled` to disable it.").queue();
                    inProgress = true;
                }
                break;
            case 4:
                //getting guild name
                if (inProgress) {
                    String guildName;
                    try {
                        guildName = GetGuild.getGuild(event.getMessage().getContentRaw());
                    } catch (Exception e) {
                        event.getChannel().sendMessage("An error occurred while validating the guild, defaulting to no guild role for now. Please try again later if you think this is a mistake\n" +
                                "Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.").queue();
                        options.put("guildMemberRole", "disabled");
                        inProgress = false;
                        return;
                    }
                    if (guildName.equals("")) {
                        event.getChannel().sendMessage("Either this user is not in a guild or an error occurred while checking this. Please try again later if you believe this is a mistake. Defaulting to no guild role for now.\n" +
                                "Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.").queue();
                        options.put("guildMemberRole", "disabled");
                    } else {
                        options.put("guildID", guildName);
                        event.getChannel().sendMessage("OK: Guild Name is now set to: `" + guildName + "`\n" +
                                "Please type `continue` to continue with the setup, `exit` to save the config and exit or `cancel` to exit without saving.").queue();
                    }
                    inProgress = false;
                    return;

                } else {
                    event.getChannel().sendMessage("Please type your Minecraft username to set your guild.").queue();
                    inProgress = true;
                }
                break;
            case 5:
                //final message
                System.out.println(options);
                Database.saveConfig(event.getGuild().getId(), options);
                Cache.refreshOrAddCache(event.getGuild().getId());
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Setup done");
                eb.setDescription("Setup finished.\n" +
                        "These are the current config options for " + event.getGuild().getName() + ":\n" +
                        showCurrentConfig());
                event.getChannel().sendMessage(eb.build()).queue();
                done = true;
                event.getJDA().removeEventListener(this);
        }
    }
}
