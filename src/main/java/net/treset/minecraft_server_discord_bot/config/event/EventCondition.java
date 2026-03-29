package net.treset.minecraft_server_discord_bot.config.event;

import java.util.function.Function;

public enum EventCondition {
    always(s -> true),
    never(s -> false),
    ifFailure(s -> s);

    private final Function<Boolean, Boolean> decider;

    EventCondition(Function<Boolean, Boolean> decider) {
        this.decider = decider;
    }

    public boolean shouldSend(boolean success) {
        return decider.apply(success);
    }
}
