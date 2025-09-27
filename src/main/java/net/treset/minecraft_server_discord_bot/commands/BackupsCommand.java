package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;

public class BackupsCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(Config.contact.backups_location != null) {
            output += String.format("Backups are available at **%s**. ", Config.contact.backups_location);
        }
        if(Config.contact.admin != null) {
            output += String.format("For more information contact **%s**.", Config.contact.admin);
        }
        if(output.isBlank()) {
            output = "No information about backups is configured.";
        }

        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }
}
