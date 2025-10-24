package net.treset.minecraft_server_discord_bot.server.data;



public class RpcAdvancement {
    private RpcPlayer player;
    private RpcText message;
    private String identifier;
    private RpcText title;
    private RpcText description;
    private RpcText toast;
    private Integer color;

    public RpcPlayer getPlayer() {
        return player;
    }

    public RpcText getMessage() {
        return message;
    }

    public String getIdentifier() {
        return identifier;
    }

    public RpcText getTitle() {
        return title;
    }

    public RpcText getDescription() {
        return description;
    }

    public RpcText getToast() {
        return toast;
    }

    public Integer getColor() {
        return color;
    }
}
