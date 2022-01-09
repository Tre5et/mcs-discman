package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.DriveTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordBot {
    public static final String CONFIG_FILE = "discordbot.conf";
    public static final String DETAILS_FILE = "details.conf";
    public static final String PLAYERS_FILE = "storage/players.storage";
    public static JDA JDA;
    public static Guild GUILD;
    public static MessageChannel BOT_CHANNEL;
    public static Role MODERATOR_ROLE;
    public static Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class);

    public static void init() {

        ConfigTools.initConfig();

        DiscordTools.initClient();

        DriveTools.initDriveClient();

        assert GUILD != null;
        DiscordTools.upsertCommands();

        assert BOT_CHANNEL != null;
        BOT_CHANNEL.sendMessage("Hi, I'm online now!").queue();

        PermanentOperations.permanentLoop();
    }
}
