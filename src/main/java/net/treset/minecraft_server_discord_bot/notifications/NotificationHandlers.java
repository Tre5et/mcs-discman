package net.treset.minecraft_server_discord_bot.notifications;

import dev.treset.mcdl.servermanagement.ManagementHandler;
import dev.treset.mcdl.servermanagement.vanilla.RpcNotifications;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.schedulers.EventScheduler;
import net.treset.minecraft_server_discord_bot.server.DiscmanRpcNotifications;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;

public class NotificationHandlers {

    public static void register() {
        ManagementHandler handler = ManagementClient.get();

        //handler.addNotificationMethod(RpcNotifications.Server.started(() -> send("Server started.")));
        //handler.addNotificationMethod(RpcNotifications.Server.stopping(CrashHandler::processStopping));

        handler.addNotificationMethod(RpcNotifications.Players.joined(p -> send(p.name() + " joined the game.")));
        handler.addNotificationMethod(RpcNotifications.Players.left(p -> send(p.name() + " left the game.")));
        handler.addNotificationMethod(DiscmanRpcNotifications.Players.death(d -> { if(d.message() != null) send(d.message().literal() + "."); }));
        handler.addNotificationMethod(DiscmanRpcNotifications.Players.advancement(a -> { if(a.message() != null) send(a.message().literal() + "."); }));
    }

    private static void send(String message) {
        DiscordBot.sendText(message, MessageOrigin.RPC);
        EventScheduler.eventOccurred();
    }
}
