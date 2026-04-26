package net.treset.minecraft_server_discord_bot.config.event;

import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

import java.util.Set;

public interface EventDiscordOutput {
    void output(String message, Set<GuildMessageChannel> channels, MessageTopLevelComponent... components);

    class Message implements EventDiscordOutput {
        @Override
        public void output(String message, Set<GuildMessageChannel> channels, MessageTopLevelComponent... components) {
            channels.forEach(c -> c.sendMessage(message).setComponents(components).queue());
        }
    }

    class Reply implements EventDiscordOutput {
        private final InteractionHook hook;

        public Reply(InteractionHook hook) {
            this.hook = hook;
        }

        @Override
        public void output(String message, Set<GuildMessageChannel> channels, MessageTopLevelComponent... components) {
            hook.sendMessage(message)
                    .setComponents(components)
                    .queue();
        }
    }

    class Interaction implements EventDiscordOutput {
        private final ComponentInteraction interaction;

        public Interaction(ComponentInteraction interaction) {
            this.interaction = interaction;
        }


        @Override
        public void output(String message, Set<GuildMessageChannel> channels, MessageTopLevelComponent... components) {
            interaction.reply(message).setComponents(components).queue();
        }
    }
}
