package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;
import java.util.List;

public class MembersCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        try {
            ConfigTools.loadDetails();
        } catch (IOException e) {
            MessageManager.log("Failed to update details before command.", LogLevel.WARN, e);
        }

        List<String> members = ConfigTools.DETAILS.members;
        String admin = ConfigTools.DETAILS.admin;

        output = String.format("Current members are: **%s**.\nTo become a member DM **%s**.", FormatTools.formatList(members, ", "), admin);

        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }
}
