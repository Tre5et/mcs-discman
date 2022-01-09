package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;

import java.util.Objects;

public class LogConsoleCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(DiscordTools.isModerator(event)) {
            boolean toggleActive = Objects.requireNonNull(event.getOption("state")).getAsBoolean();

            if(toggleActive) {
                if(PermanentOperations.isLoggingFull){
                    output = "Full logging is already enabled.";
                    DiscordBot.LOGGER.info("ConsoleLogging Command handled: no state change: " + Objects.requireNonNull(event.getOption("state")).getAsString());

                } else {
                    PermanentOperations.isLoggingFull = true;
                    output = "Full console logging enabled.";
                    DiscordBot.LOGGER.info("ConsoleLogging Command handled: success: " + Objects.requireNonNull(event.getOption("state")).getAsString());
                }
            } else {
                if(!PermanentOperations.isLoggingFull) {
                    output = "Full console logging is already disabled.";
                    DiscordBot.LOGGER.info("ConsoleLogging Command handled: no state change: " + Objects.requireNonNull(event.getOption("state")).getAsString());
                } else {
                    PermanentOperations.isLoggingFull = false;
                    output = "Full console logging disabled.";
                    DiscordBot.LOGGER.info("ConsoleLogging Command handled: success: " + Objects.requireNonNull(event.getOption("state")).getAsString());
                }
            }

        } else {
            output = "You don't have permission to do that.";
            DiscordBot.LOGGER.info("ConsoleLogging Command handled: permission required: " + Objects.requireNonNull(event.getOption("state")).getAsString());
        }

        event.reply(output).queue();
    }
}
