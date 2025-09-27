package net.treset.minecraft_server_discord_bot.notifications;

import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.server.RpcMessager;
import net.treset.minecraft_server_discord_bot.server.schemas.RpcNotification;

import java.util.Map;
import java.util.function.Function;

public class NotificationHandlers {
    public static void register() {
        registerHandler("server/started", "Server started.");
        registerHandler("server/stopping", "Server stopping...");
        registerHandler("server/stopped", "Server stopped.");

        registerHandler("players/joined", n -> getPlayerName(n) + " joined the game.");
        registerHandler("players/left", n -> getPlayerName(n) + " left the game.");
    }

    private static String getPlayerName(RpcNotification notification) {
        if(notification.params().size() == 1
                && notification.params().get(0) instanceof Map<?, ?> map
        ) {
            if(map.containsKey("name")) {
                return map.get("name").toString();
            }
        }
        return "Unknown player";
    }

    private static void registerHandler(String path, String message) {
        registerHandler(path, r -> message);
    }

    private static void registerHandler(String path, Function<RpcNotification, String> handler) {
        RpcMessager.addNotificationHandler(
                "minecraft:notification/" + path,
                n -> {
                    DiscordBot.sendText(handler.apply(n), MessageOrigin.RPC);
                    PermanentOperations.setSomethingHappened();
                }
        );
    }
}
