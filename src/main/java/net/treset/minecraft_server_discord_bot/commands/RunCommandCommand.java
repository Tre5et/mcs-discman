package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

import java.util.Objects;

public class RunCommandCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(DiscordTools.isModerator(event)) {
            String cmd = Objects.requireNonNull(event.getOption("command")).getAsString();

            ServerTools.runServerCommand(cmd);

            output = String.format("Successfully ran command **%s**.", cmd);
        } else {
            output = "You don't have permission to do that.";
        }

        event.reply(output).queue();

        DiscordBot.LOGGER.info("RunCommand Command handled: event.getOption(\"command\").getAsString() = " + Objects.requireNonNull(event.getOption("command")).getAsString());
    }
}
