package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

import java.util.Objects;

public class RunCommandCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        String cmd = Objects.requireNonNull(event.getOption("command")).getAsString();

        if(DiscordTools.isModerator(event)) {

            ServerTools.runServerCommand(cmd);

            output = String.format("Successfully ran command **%s**.", cmd);
            event.getHook().sendMessage(output).queue();

            MessageManager.log(String.format("Handled. Ran command \"%s\".", cmd), LogLevel.INFO);
        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();

            MessageManager.log(String.format("Handled. Permission denied for command \"%s\".", cmd), LogLevel.INFO);
        }
    }
}
