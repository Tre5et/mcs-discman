package net.treset.minecraft_server_discord_bot.rpc.schemas;

import java.util.Objects;

public interface RpcResponse {
    String jsonrpc();
    Integer id();
    Object result();
    RpcError error();

    default boolean hasResponse() {
        return result() != null;
    }

    default boolean isResult(Object result) {
        return Objects.equals(result(), result);
    }

    static RpcResponse Timeout(int id) {
        return new RpcResponse() {
            @Override
            public String jsonrpc() {
                return "2.0";
            }

            @Override
            public Integer id() {
                return id;
            }

            @Override
            public Object result() {
                return null;
            }

            @Override
            public RpcError error() {
                return new RpcError(-1, "Timed out", "Server took more than 10 seconds to respond");
            }
        };
    }
}
