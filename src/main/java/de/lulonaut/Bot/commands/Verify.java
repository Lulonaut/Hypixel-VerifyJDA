package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.utils.API;
import de.lulonaut.Bot.utils.Conf;
import de.lulonaut.Bot.utils.Config;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

/**
 * <h1>Verify Command</h1>
 * This Command takes a Username and then checks their Linked Discord on Hypixel.
 * If it matches their Discord Tag they get a role and some other stuff happens depending on the Config
 *
 * @see Config
 */

public class Verify extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //set logging level
        getLogger("").setLevel(Level.INFO);
        getLogger("").getHandlers()[0].setLevel(Level.INFO);


        //Checking if it's the actual command:
        String[] msg = event.getMessage().getContentRaw().split(" ");
        String[] APIResult;
        String UserDiscord = Objects.requireNonNull(event.getMember()).getUser().getAsTag();

        if (!msg[0].equalsIgnoreCase(Conf.PREFIX + "verify")) {
            return;
        }
        //Checking correct usage
        if (msg.length != 2) {
            event.getChannel().sendMessage("Usage: " + Conf.PREFIX + "verify [Minecraft IGN]").queue();
            return;
        }

        //Command logic
        try {
            //Getting the linked Discord + Error handling
            APIResult = API.getStuff(msg[1], Conf.Endpoint);
            if (APIResult[0].equals("error")) {
                event.getChannel().sendMessage("There was an Error while checking your linked Discord, please try again later! (API probably down)").queue();
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage("Some error occurred, API is probably down. Please try again later").queue();
            return;
        }
        // Discord obtained, checking if it matches their Discord
        String Discord = APIResult[0];
        String Nickname = APIResult[1];
        String Rank = APIResult[2];
        String Guild = APIResult[3];
        System.out.println(Guild);
        int ErrorCount = 0;
        int Errors = 0;

        //Case: Discord is null (not Linked anything)
        if (Discord.equals("null")) {
            event.getChannel().sendMessage("Looks you didn't link a Discord yet. If you don't know how to add one please type '" + Conf.PREFIX + "linkdc'. If you just changed this please wait a few minutes and try again. (Spamming it won't do anything)").queue();
        }

        //Case : Discord doesn't match
        else if (!UserDiscord.equals(Discord)) {
            event.getChannel().sendMessage("Your Discord Tag is: `" + UserDiscord + "`. But a wise man told me you linked this Discord in Minecraft: `" + Discord + "`. If you just changed this please wait a few minutes and try again. (Spamming it won't do anything)").queue();
        }

        //Case : Discord does match
        else {
            try {
                //Add Role(s)
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName(Conf.VerifyRole, false).get(0)).queue();
                if (!(Conf.OptionalRole == null)) {
                    event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName(Conf.OptionalRole, false).get(0)).queue();
                }

                //Change Nickname
                event.getMember().modifyNickname(Nickname).queue();

                //Add Role for Rank if enabled
                if (Conf.RankRoles) {
                    switch (Rank) {
                        case "VIP_PLUS":
                            Rank = "VIP+";
                            break;
                        case "MVP_PLUS":
                            Rank = "MVP+";
                            break;
                        case "MVP_PLUS_PLUS":
                            Rank = "MVP++";
                            break;
                    }

                    try {
                        if (!Rank.equals("null")) {
                            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName(Rank, true).get(0)).queue();
                        }
                    } catch (Exception e) {
                        event.getChannel().sendMessage("Looks like a rank role does not exist, please ask a Staff Member to add the following Role: `" + Rank + "`").queue();
                    }
                }

                if (Conf.GuildRoles && Guild.equals(Conf.Guild)) {
                    try {
                        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName(Conf.GuildRole, true).get(0)).queue();
                    } catch (Exception e) {
                        event.getChannel().sendMessage("Looks like a role called " + Conf.GuildRole + " doesn't exist. Please ask an Admin to add it!").queue();
                    }
                }

            } catch (HierarchyException e) {
                ErrorCount++;
            } catch (Exception e) {
                e.printStackTrace();
                Errors++;
            }

            if (ErrorCount > 0) {
                event.getChannel().sendMessage("You have higher Perms than me so i couldn't change much. But your Discord matches the one linked on Minecraft :D").queue();

            } else if (Errors > 0) {
                event.getChannel().sendMessage("Some internal error happened, please contact a Admin :(").queue();
            } else {
                event.getChannel().sendMessage("I added a Role, changed your Nickname etc.").queue();
            }
        }
    }
}