package net.treset.minecraft_server_discord_bot.logging;

import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcMessage;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcSystemMessage;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;

public interface OutputConsumer {
    void accept(OutputType type, String message);

    static OutputConsumer discord(MessageOrigin origin) {
        return (t,m) -> {
            if(t.shouldNotifyDiscord()) {
                DiscordBot.sendText(m, origin);
            }
        };
    }

    static OutputConsumer discordResponse(MessageOrigin origin, InteractionHook hook) {
        return (t, m) -> {
            if(t.shouldNotifyDiscord()) {
                hook.sendMessage(m).queue();
            }
        };
    }

    static OutputConsumer inGame(MessageOrigin origin) {
        return (t,m) -> {
            if(ManagementClient.get().isConnected() && t.shouldNotifyPlayers()) {
                ManagementClient.get().send(
                        RpcMethods.Server.SYSTEM_MESSAGE,
                        new RpcSystemMessage(null, false, new RpcMessage(null, null, "[Discman] " + m)),
                        r -> {},
                        e -> Logger.warn(e, "Failed to send message %s to server", m)
                );
            }
        };
    }

    static OutputConsumer all(MessageOrigin origin) {
        OutputConsumer d = OutputConsumer.discord(origin);
        OutputConsumer g = OutputConsumer.inGame(origin);
        return (t,m) -> {
            d.accept(t,m);
            g.accept(t,m);
        };
    }

    static OutputConsumer allResponse(MessageOrigin origin, InteractionHook hook) {
        OutputConsumer d = OutputConsumer.discordResponse(origin, hook);
        OutputConsumer g = OutputConsumer.inGame(origin);
        return (t,m) -> {
            d.accept(t,m);
            g.accept(t,m);
        };
    }
}
