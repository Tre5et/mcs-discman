package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.BackupHandler;
import net.treset.minecraft_server_discord_bot.system.*;

import java.time.format.DateTimeFormatter;

public class CreateBackupCommand {
    private static final DateTimeFormatter backupNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");

    public static void handleCommand(SlashCommandEvent event) {
        String output;
        if(DiscordBot.isModerator(event)) {
            new Thread(() -> BackupHandler.execute(
                    BackupHandler.Mode.WHILE_RUNNING,
                    backupNameFormatter::format,
                    m -> event.getHook().sendMessage(m).queue()
            )).start();
        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();
            Logger.info("Handled.");
        }
    }
}
