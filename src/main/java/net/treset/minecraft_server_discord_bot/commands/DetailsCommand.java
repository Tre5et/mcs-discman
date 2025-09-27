package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.rpc.MessageHandler;
import net.treset.minecraft_server_discord_bot.rpc.data.RpcStatus;
import net.treset.minecraft_server_discord_bot.rpc.schemas.RpcResponse;

import java.io.IOException;

public class DetailsCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        String version = getVersion();
        if(version == null) {
            output = "Failed to get details!";
            event.getHook().sendMessage(output).queue();
            return;
        }

        output = String.format("The server is running version **%s**.",  version);
        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }

    private static String getVersion() {
        try {
            RpcResponse res = MessageHandler.request("minecraft:server/status");
            try {
                return RpcStatus.from(res.result()).version().name();
            } catch (IOException e) {
                MessageManager.log("Failed to extract version for details command.", LogLevel.WARN, e);
                return null;
            }
        } catch (IOException e) {
            MessageManager.log("Failed to get version for details command.", LogLevel.WARN, e);
            return null;
        }
    }
}
