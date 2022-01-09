package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;

public class BackupsCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        ConfigTools.loadDetails();
        String backupLocation = ConfigTools.DETAILS.backupLocation;
        String admin = ConfigTools.DETAILS.admin;

        if(ConfigTools.CONFIG.DRIVE_UPLOAD && !backupLocation.equals("")) output = String.format("Backups are available at **%s**.\nFurther backups can be requested from %s.", backupLocation, admin);
        else output = String.format("Backups can be requested from %s.", admin);

        event.reply(output).queue();

        DiscordBot.LOGGER.info("Backups Command handled.");
    }
}
