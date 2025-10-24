package net.treset.minecraft_server_discord_bot.commands;

import dev.treset.mcdl.servermanagement.request.RpcResponse;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.data.RpcStatus;

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

        Logger.info("Handled.");
    }

    private static String getVersion() {
        try {
            RpcResponse res = ManagementClient.get().request("minecraft:server/status");
            try {
                return RpcStatus.from(res).getVersion().getName();
            } catch (IOException e) {
                Logger.warn(e, "Failed to extract version for details command.");
                return null;
            }
        } catch (IOException e) {
            Logger.warn(e, "Failed to get version for details command.");
            return null;
        }
    }
}
