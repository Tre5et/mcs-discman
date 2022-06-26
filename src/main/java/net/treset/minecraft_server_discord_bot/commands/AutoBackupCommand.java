package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;

import java.util.Objects;

public class AutoBackupCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";
        if(DiscordTools.isModerator(event)) {
            if(Objects.requireNonNull(event.getOption("state")).getAsBoolean()) {
                if(PermanentOperations.isBackupEnabled) {
                    output = "Auto-Backup is already enabled.";
                    event.reply(output).queue();
                    MessageManager.log("Handled. Already active.", LogLevel.INFO);
                } else {
                    PermanentOperations.isBackupEnabled = true;
                    output = "Enabled Auto-Backup";
                    event.reply(output).queue();
                    MessageManager.log("Handled. Enabled.", LogLevel.INFO);
                }
            } else {
                if(PermanentOperations.isBackupEnabled) {
                    PermanentOperations.isBackupEnabled = false;
                    output = "Disabled Auto-Backup";
                    event.reply(output).queue();
                    MessageManager.log("Handled. Disabled.", LogLevel.INFO);
                } else {
                    output = "Auto-Backup is already disabled.";
                    event.reply(output).queue();
                    MessageManager.log("Handled. Already inactive.", LogLevel.INFO);
                }
            }
        } else {
            output = "You don't have permission to do that.";
            event.reply(output).queue();
            MessageManager.log("Handled. Permission required.", LogLevel.INFO);
        }
    }
}
