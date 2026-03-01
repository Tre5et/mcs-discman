package net.treset.minecraft_server_discord_bot.logging;

public enum OutputType {
    IMPORTANT(true, true),
    ALL(true, true),
    PLAYERS(true, false),
    META(false, true);

    public final boolean notifyPlayers;
    public final boolean notifyDiscord;

    OutputType(boolean notifyPlayers, boolean notifyDiscord) {
        this.notifyPlayers = notifyPlayers;
        this.notifyDiscord = notifyDiscord;
    }

    public boolean shouldNotifyPlayers() {
        return notifyPlayers;
    }

    public boolean shouldNotifyDiscord() {
        return notifyDiscord;
    }
}
