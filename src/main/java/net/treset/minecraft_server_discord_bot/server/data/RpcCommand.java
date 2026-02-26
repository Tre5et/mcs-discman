package net.treset.minecraft_server_discord_bot.server.data;


import com.google.gson.annotations.SerializedName;

public record RpcCommand(
        RpcText message,
        CommandStatus status
) {
    public enum CommandStatus {
        @SerializedName("success") SUCCESS,
        @SerializedName("failure") FAILURE,
        @SerializedName("no_response") NO_RESPONSE
    }
}
