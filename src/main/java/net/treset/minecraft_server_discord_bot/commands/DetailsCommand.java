package net.treset.minecraft_server_discord_bot.commands;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcVersion;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;

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
            RpcVersion res = ManagementClient.get().request(RpcMethods.Server.STATUS).version();
            return res == null ? null : res.name();
        } catch (RpcCommunicationException e) {
            Logger.warn(e, "Failed to get version for details command", e);
            return null;
        }
    }
}
