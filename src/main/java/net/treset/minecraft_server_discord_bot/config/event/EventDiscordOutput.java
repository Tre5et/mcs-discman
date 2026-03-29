package net.treset.minecraft_server_discord_bot.config.event;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.Set;

public interface EventDiscordOutput {
    void output(String message, Set<MessageChannel> channels);

    class Message implements EventDiscordOutput {
        @Override
        public void output(String message, Set<MessageChannel> channels) {
            channels.forEach(c -> c.sendMessage(message).queue());
        }
    }

    class Reply implements EventDiscordOutput {
        private final InteractionHook hook;

        public Reply(InteractionHook hook) {
            this.hook = hook;
        }

        @Override
        public void output(String message, Set<MessageChannel> channels) {
            hook.sendMessage(message).queue();
        }
    }
}
