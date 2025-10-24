package net.treset.minecraft_server_discord_bot.notifications;

import dev.treset.mcdl.servermanagement.ManagementHandler;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.data.RpcAdvancement;
import net.treset.minecraft_server_discord_bot.server.data.RpcDeath;
import net.treset.minecraft_server_discord_bot.server.data.RpcPlayer;

public class NotificationHandlers {

    public static void register() {
        ManagementHandler handler = ManagementClient.get();

        handler.addNotificationHandler("minecraft:notification/server/started", () -> send("Server started."));
        handler.addNotificationHandler("minecraft:notification/server/stopping", () -> send("Server stopping..."));
        handler.addNotificationHandler("minecraft:notification/server/stopped", () -> send("Server stopped."));

        handler.addNotificationHandler("minecraft:notification/players/joined", RpcPlayer.class,
                p -> send(p.getName() + " joined the game.")
        );
        handler.addNotificationHandler("minecraft:notification/players/left", RpcPlayer.class,
                p -> send(p.getName() + " left the game.")
        );

        handler.addNotificationHandler("discman:notification/players/death", RpcDeath.class,
                d -> { if(d.getMessage() != null) send(d.getMessage().getLiteral() + "."); }
        );
        handler.addNotificationHandler("discman:notification/players/advancement", RpcAdvancement.class,
                d -> { if(d.getMessage() != null) send(d.getMessage().getLiteral() + "."); }
        );
    }

    private static void send(String message) {
        DiscordBot.sendText(message, MessageOrigin.RPC);
        PermanentOperations.setSomethingHappened();
    }
}
