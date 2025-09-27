package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.Objects;

public class SayCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String message = Objects.requireNonNull(event.getOption("message")).getAsString();
        if(DiscordBot.isModerator(event)) {
            event.getHook().sendMessage(message).queue();

            Logger.info("Handled. Said \"%s\".", message);
        } else {
            String output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();

            Logger.info("Handled. Permission required to say \"%s\".", message);
        }
    }
}
