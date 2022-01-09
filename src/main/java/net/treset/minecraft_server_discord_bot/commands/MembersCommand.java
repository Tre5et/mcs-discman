package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.util.List;

public class MembersCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        ConfigTools.loadDetails();
        List<String> members = ConfigTools.DETAILS.members;
        String admin = ConfigTools.DETAILS.admin;

        output = String.format("Current members are: **%s**.\nTo become a member DM %s.", FormatTools.formatList(members, ", "), admin);

        event.reply(output).queue();

        DiscordBot.LOGGER.info("Members Command handled.");
    }
}
