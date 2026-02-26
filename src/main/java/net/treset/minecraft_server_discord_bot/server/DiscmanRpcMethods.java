package net.treset.minecraft_server_discord_bot.server;

import com.google.gson.reflect.TypeToken;
import dev.treset.mcdl.servermanagement.outgoing.OutgoingMethod;
import net.treset.minecraft_server_discord_bot.server.data.RpcCommand;

public class DiscmanRpcMethods {
    public static class Server {
        public static OutgoingMethod<String, RpcCommand> COMMAND_RUN = OutgoingMethod.of("discman:server/command/run").withParameterAndResponse(new TypeToken<>() {}, new TypeToken<>() {});

    }
}
