package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;

public class PingCommand {
    public static void handleCommand(SlashCommandEvent event) {
        event.reply("Pong!").queue();

        DiscordBot.LOGGER.info("Ping Command handled.");
    }
}
