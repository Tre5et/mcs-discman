package net.treset.minecraft_server_discord_bot.exception;

import java.io.IOException;

public class ServerOperationException extends IOException {
    public ServerOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerOperationException(String message) {
        super(message);
    }
}
