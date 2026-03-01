package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.logging.Logger;

public class BackupsCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(Config.get().backup.publicLocation != null) {
            output += String.format("Backups are available at **%s**. ", Config.get().backup.publicLocation);
        }
        if(Config.get().discord.admin != null) {
            output += String.format("For more information contact **%s**.", Config.get().discord.admin);
        }
        if(output.isBlank()) {
            output = "No information about backups is configured.";
        }

        event.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }
}
