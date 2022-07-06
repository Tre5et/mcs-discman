package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;

import java.util.Objects;

public class SayCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String message = Objects.requireNonNull(event.getOption("message")).getAsString();
        if(DiscordTools.isModerator(event)) {
            event.getHook().sendMessage(message).queue();

            MessageManager.log(String.format("Handled. Said \"%s\".", message), LogLevel.INFO);
        } else {
            String output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();

            MessageManager.log(String.format("Handled. Permission required to say \"%s\".", message), LogLevel.INFO);
        }
    }
}
