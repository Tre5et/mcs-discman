package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;

import java.io.IOException;

public class BackupsCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        try {
            ConfigTools.loadDetails();
        } catch (IOException e) {
            MessageManager.log("Failed to update details before command.", LogLevel.WARN, e);
        }

        String backupLocation = ConfigTools.DETAILS.backupLocation;
        String admin = ConfigTools.DETAILS.admin;

        if(ConfigTools.CONFIG.DRIVE_UPLOAD && !backupLocation.isEmpty()) output = String.format("Backups are available at **%s**.\nFurther backups can be requested from %s.", backupLocation, admin);
        else output = String.format("Backups can be requested from %s.", admin);

        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }
}
