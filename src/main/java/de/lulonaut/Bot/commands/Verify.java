package de.lulonaut.Bot.commands;

import de.lulonaut.Bot.Main;
import de.lulonaut.Bot.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h1>Verify Command</h1>
 * This Command takes a Username and then checks their Linked Discord on Hypixel.
 * If it matches their Discord Tag they get a role and some other stuff happens depending on the Config
 *
 * @see de.lulonaut.Bot.utils.Config
 */

public class Verify extends ListenerAdapter {
    Logger logger = Logger.getLogger("logger");

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Checking if it's the actual command:
        String[] msg = event.getMessage().getContentRaw().split(" ");
        String[] APIResult;
        String UserDiscord = Objects.requireNonNull(event.getMember()).getUser().getAsTag();

        if (!msg[0].equalsIgnoreCase(Main.PREFIX + "verify")) {
            return;
        }
        //Checking correct usage
        if (msg.length != 2) {
            event.getChannel().sendMessage("Usage: " + Main.PREFIX + "verify [Minecraft IGN]").queue();
            return;
        }

        logger.log(Level.FINE, "Verify Command is now being executed for Discord User: %s", UserDiscord);
        //Command logic
        try {
            //Getting the linked Discord + Error handling
            logger.log(Level.FINE, "Contacting API...");
            APIResult = API.getStuff(msg[1], Main.Endpoint);
            if (APIResult[0].equals("error")) {
                event.getChannel().sendMessage("There was an Error while checking your linked Discord, please try again later! (API probably down)").queue();
                logger.log(Level.SEVERE, "Error while contacting API. Request for Username: %s", msg[1]);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage("Some error occurred, maybe the API is down. Please try again later").queue();
            logger.log(Level.SEVERE, "Error while contacting API. Request for Username: %s", msg[1]);
            return;
        }

        //Discord obtained, checking if it matches their Discord
        String Discord = APIResult[0];
        String Nickname = APIResult[1];
        String Rank = APIResult[2];
        String Guild = APIResult[3];
        System.out.println(Guild);
        int ErrorCount = 0;
        int Errors = 0;

        //Case: Discord is null (not Linked anything)
        if (Discord.equals("null")) {
            //TODO add Command linkdc
            event.getChannel().sendMessage("Looks you didn't link a Discord yet. // If you don't know how to add one please type '" + Main.PREFIX + "linkdc'. If you just changed this please wait a few minutes and try again. (Spamming it won't do anything)").queue();
        }

        //Case : Discord doesn't match
        else if (!UserDiscord.equals(Discord)) {
            event.getChannel().sendMessage("Your Discord Tag is: `" + UserDiscord + "`. But a wise man told me you linked this Discord in Minecraft: `" + Discord + "`. If you just changed this please wait a few minutes and try again. (Spamming it won't do anything)").queue();
        }

        //Case : Discord does match
        else {
            try {
                //Add Role(s)
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName(Main.VerifyRole, false).get(0)).queue();
                if (!(Main.OptionalRole == null)) {
                    event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName(Main.OptionalRole, false).get(0)).queue();
                }

                //Change Nickname
                event.getMember().modifyNickname(Nickname).queue();

                //Add Role for Rank if enabled
                if (Main.RankRoles) {
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

                //if (Main.GuildRoles && Guild.equals(Main.Guild)) {
                //
                //}

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