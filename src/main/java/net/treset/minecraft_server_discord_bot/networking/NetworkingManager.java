package net.treset.minecraft_server_discord_bot.networking;

public class NetworkingManager {

    public static void init() {
        if(!ConnectionManager.openConnection()) return;

        CommunicationManager.handleData();
    }
}
