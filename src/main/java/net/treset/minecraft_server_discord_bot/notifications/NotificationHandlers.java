package net.treset.minecraft_server_discord_bot.notifications;

import dev.treset.mcdl.servermanagement.ManagementHandler;
import dev.treset.mcdl.servermanagement.vanilla.RpcNotifications;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.server.DiscmanRpcNotifications;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;

public class NotificationHandlers {

    public static void register() {
        ManagementHandler handler = ManagementClient.get();

        handler.addNotificationMethod(RpcNotifications.Server.started(() -> Config.get().events.started.send()));
        handler.addNotificationMethod(RpcNotifications.Server.stopping(() -> Config.get().events.stopping.send()));
        handler.addNotificationMethod(RpcNotifications.Players.joined(p -> Config.get().events.joined.send(p)));
        handler.addNotificationMethod(RpcNotifications.Players.left(p -> Config.get().events.left.send(p)));
        handler.addNotificationMethod(DiscmanRpcNotifications.Players.death(d -> Config.get().events.death.send(d)));
        handler.addNotificationMethod(DiscmanRpcNotifications.Players.advancement(a -> Config.get().events.advancement.send(a)));
    }
}
