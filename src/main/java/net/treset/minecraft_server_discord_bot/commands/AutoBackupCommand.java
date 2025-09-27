package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.Objects;

public class AutoBackupCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;
        if(DiscordBot.isModerator(event)) {
            if(Objects.requireNonNull(event.getOption("state")).getAsBoolean()) {
                if(PermanentOperations.isBackupEnabled) {
                    output = "Auto-Backup is already enabled.";
                    event.getHook().sendMessage(output).queue();
                    Logger.info("Handled. Already active.");
                } else {
                    PermanentOperations.isBackupEnabled = true;
                    output = "Enabled Auto-Backup";
                    event.getHook().sendMessage(output).queue();
                    Logger.info("Handled. Enabled.");
                }
            } else {
                if(PermanentOperations.isBackupEnabled) {
                    PermanentOperations.isBackupEnabled = false;
                    output = "Disabled Auto-Backup";
                    event.getHook().sendMessage(output).queue();
                    Logger.info("Handled. Disabled.");
                } else {
                    output = "Auto-Backup is already disabled.";
                    event.getHook().sendMessage(output).queue();
                    Logger.info("Handled. Already inactive.");
                }
            }
        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();
            Logger.info("Handled. Permission required.");
        }
    }
}
