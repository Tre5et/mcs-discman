package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;

import java.util.Objects;

public class SayCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(DiscordTools.isModerator(event)) {
            output = Objects.requireNonNull(event.getOption("message")).getAsString();
            DiscordBot.LOGGER.info("Say Command handled: success: " + Objects.requireNonNull(event.getOption("message")).getAsString());
        } else {
            output = "You don't have permission to do that.";
            DiscordBot.LOGGER.info("Say Command handled: permission required: " + Objects.requireNonNull(event.getOption("message")).getAsString());
        }

        event.reply(output).queue();
    }
}
