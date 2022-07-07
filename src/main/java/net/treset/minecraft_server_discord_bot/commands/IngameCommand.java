package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.networking.CommunicationManager;

import java.util.Objects;

public class IngameCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        String type = Objects.requireNonNull(event.getOption("action")) .getAsString();
        switch(type) {
            case "time" -> output = getTime();
            default -> output = "not found";
        }

        event.getHook().sendMessage(output).queue();
    }

    private static String getTime() {
        int time = CommunicationManager.getTime();

        if(time == -2) {
            MessageManager.log("Handled get time command. No client connected.", LogLevel.INFO);
            return "No client is connected to get the time from. This command isn't available until it is connected.";
        } else if(time == -1) {
            MessageManager.log("Error handling get time command. Unknown.", LogLevel.WARN);
            return "Something went wrong getting the time. Try again.";
        }

        time = time % 24000;

        int hours = (time / 1000 + 6) % 24;
        int minutes = (int)(((float)time % 1000) / 1000 * 60) % 60;

        MessageManager.log(String.format("Handled get time command at %s ticks.", time), LogLevel.INFO);
        return String.format("The current time is %02d:%02d (%s ticks).", hours, minutes, time);
    }
}
