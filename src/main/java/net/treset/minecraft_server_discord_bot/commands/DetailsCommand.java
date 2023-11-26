package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;
import java.util.List;

public class DetailsCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        try {
            ConfigTools.loadDetails();
        } catch (IOException e) {
            MessageManager.log("Failed to update details before command.", LogLevel.WARN, e);
        }

        String version = ConfigTools.DETAILS.version;
        String server = ConfigTools.DETAILS.server;
        List<String> mods = ConfigTools.DETAILS.mods;

        output = String.format("The server is running **%s** for version **%s** with %s.", server, version, (mods.isEmpty()) ? "no mods" : "the mods: **" + FormatTools.formatList(mods, ", ") + "**");

        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }
}
