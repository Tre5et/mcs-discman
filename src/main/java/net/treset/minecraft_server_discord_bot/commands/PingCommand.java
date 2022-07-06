package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;

public class PingCommand {
    public static void handleCommand(SlashCommandEvent event) {
        event.getHook().sendMessage("Pong!").queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }
}
