package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.tools.FileTools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final String CONFIG_FILE = "discordbot.conf";
    private static final String DEBUG_CONFIG = "debug/discordbot.conf";

    public static CommunicationConfig communication;

    public static void load() throws IOException {
        String file = CONFIG_FILE;
        if(Files.exists(Path.of(DEBUG_CONFIG))) {
            file = DEBUG_CONFIG;
        }

        if(!Files.exists(Path.of(file))) {
            throw new FileNotFoundException("Unable to find config file " + file);
        }

        String config = FileTools.readFile(file);

        communication = new CommunicationConfig(config);
    }

}
