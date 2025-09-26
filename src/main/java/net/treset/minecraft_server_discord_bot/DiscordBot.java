package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.rpc.ConnectionManager;
import net.treset.minecraft_server_discord_bot.rpc.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class DiscordBot {
    public static String CONFIG_FILE = "discordbot.conf";
    public static String DETAILS_FILE = "details.conf";
    public static final String PLAYERS_FILE = "storage/players.storage";
    public static JDA JDA;
    public static Guild GUILD;
    public static MessageChannel BOT_CHANNEL;
    public static Role MODERATOR_ROLE;
    public static Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class);

    public static void init() {

        try {
            Config.load();
        } catch (IOException e) {
            MessageManager.log("Failed to initialize bot: Failed to load config.", LogLevel.ERROR, e);
            return;
        }

        try {
            ConnectionManager.connect();
        } catch (IOException e) {
            LOGGER.error("Failed to connect to the discord server.", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(ConnectionManager::disconnect));

        MessageHandler.addNotificationHandler(
                "minecraft:notification/players/joined",
                n -> System.out.println("JOINED!!!!!")
        );

        try {
            MessageHandler.send(
                    "minecraft:server/system_message",
                    (r,e) -> System.out.println(Objects.equals(r, true) ? "Yay" : "Nay" + e),
                    Map.of("message", Map.of("literal", "test3"), "overlay", false)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*try {
            DiscordTools.initClient();
        } catch (LoginException | InterruptedException e) {
            MessageManager.log("Failed to initialize bot: Failed setup discord.", LogLevel.ERROR, e);
            return;
        }

        new Thread(NetworkingManager::init).start();

        DriveTools.initDriveClient();

        assert GUILD != null;
        DiscordTools.upsertCommands();

        assert BOT_CHANNEL != null;
        MessageManager.sendText("Hi, I'm online now.", MessageOrigin.SCHEDULE);

        new Thread(PermanentOperations::permanentLoop).start();*/
    }
}
