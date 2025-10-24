package net.treset.minecraft_server_discord_bot.server.data;

import java.util.List;

public class RpcText {
    private String literal;
    private String key;
    private List<RpcText> args;

    public String getLiteral() {
        return literal;
    }

    public String getKey() {
        return key;
    }

    public List<RpcText> getArgs() {
        return args;
    }
}
