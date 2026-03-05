package net.treset.minecraft_server_discord_bot.exception;

public class UploadException extends Exception {
    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
