package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
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
                    MessageManager.log("Handled. Already active.", LogLevel.INFO);

                } else {
                    PermanentOperations.isLoggingFull = true;
                    output = "Full console logging enabled.";
                    MessageManager.log("Handled. Enabled.", LogLevel.INFO);
                }
            } else {
                if(!PermanentOperations.isLoggingFull) {
                    output = "Full console logging is already disabled.";
                    MessageManager.log("Handled. Already inactive.", LogLevel.INFO);
                } else {
                    PermanentOperations.isLoggingFull = false;
                    output = "Full console logging disabled.";
                    MessageManager.log("Handled. Disabled.", LogLevel.INFO);
                }
            }

        } else {
            output = "You don't have permission to do that.";
            MessageManager.log("Handled. Permission required.", LogLevel.INFO);
        }

        event.getHook().sendMessage(output).queue();
    }
}
