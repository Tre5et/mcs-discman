package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;

import java.io.IOException;

public class JoinCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        try {
            ConfigTools.loadDetails();
        } catch (IOException e) {
            MessageManager.log("Failed to update details before command.", LogLevel.WARN, e);
        }

        String url = ConfigTools.DETAILS.url;

        output = String.format("Join the server using the ip-adress **%s**.\nYou must be member to join. Type ``/members`` for more details.", url);

        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }
}
