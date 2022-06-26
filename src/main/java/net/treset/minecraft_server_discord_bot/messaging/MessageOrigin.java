package net.treset.minecraft_server_discord_bot.messaging;

public enum MessageOrigin {
    LOG_FILE("from log file"),
    CLIENT("provided by client"),
    COMMAND("from a command");

    private String message;

    MessageOrigin(String message) {
        this.message = message;
    }

    public String getMessage() { return this.message; }
}
