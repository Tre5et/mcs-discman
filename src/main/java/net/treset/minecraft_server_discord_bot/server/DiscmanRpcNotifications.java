package net.treset.minecraft_server_discord_bot.server;

import com.google.gson.reflect.TypeToken;
import dev.treset.mcdl.servermanagement.incoming.IncomingReceiver;
import net.treset.minecraft_server_discord_bot.server.data.RpcAdvancement;
import net.treset.minecraft_server_discord_bot.server.data.RpcDeath;

import java.util.function.Consumer;

public class DiscmanRpcNotifications {
    public static class Players {
        public static IncomingReceiver.Notification<RpcDeath> death(Consumer<RpcDeath> resultConsumer) {
            return IncomingReceiver.notification("discman:notification/players/death", new TypeToken<>() {}, resultConsumer);
        }

        public static IncomingReceiver.Notification<RpcAdvancement> advancement(Consumer<RpcAdvancement> resultConsumer) {
            return IncomingReceiver.notification("discman:notification/players/advancement", new TypeToken<>() {}, resultConsumer);
        }
    }
}
