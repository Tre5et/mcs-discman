package net.treset.minecraft_server_discord_bot.tools;

import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.io.ClientConfig;
import net.treset.minecraft_server_discord_bot.io.DiscordbotConfig;
import net.treset.minecraft_server_discord_bot.io.PermanentOperationsConfig;
import net.treset.minecraft_server_discord_bot.io.ServerDetails;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ConfigTools {
    public static DiscordbotConfig CONFIG;
    public static PermanentOperationsConfig PERMA_CONFIG;
    public static ClientConfig CLIENT_CONFIG;
    public static ServerDetails DETAILS;
    public static List<String> PLAYERS = new ArrayList<>();

    public static void initConfig() {
        getConfig();
        loadConfig();
        loadPermanentOperationsConfig();
        loadClientConfig();
        loadDetails();
        loadPlayers();
    }

    private static void getConfig() {
        if(FileTools.fileExists("discordbot_test.conf")) {
            DiscordBot.CONFIG_FILE = "discordbot_test.conf";
            DiscordBot.DETAILS_FILE = "details_test.conf";
        }
    }

    public static void loadConfig() {
        String config = FileTools.readFile(DiscordBot.CONFIG_FILE);

        CONFIG = new DiscordbotConfig(config);
    }

    public static void loadPermanentOperationsConfig() {
        String config = FileTools.readFile(DiscordBot.CONFIG_FILE);

        PERMA_CONFIG = new PermanentOperationsConfig(config);
    }

    public static void loadClientConfig() {
        String config = FileTools.readFile(DiscordBot.CONFIG_FILE);

        CLIENT_CONFIG = new ClientConfig(config);
    }

    public static void loadDetails() {
        String config = FileTools.readFile(DiscordBot.DETAILS_FILE);

        DETAILS = new ServerDetails(config);
    }

    public static void loadPlayers() {
        String players = FileTools.readFile(DiscordBot.PLAYERS_FILE);
        players = players.replaceAll("\n", "").replaceAll("\r", "");

        String[] playerArray = players.split(",");

        PLAYERS = new LinkedList<>(Arrays.asList(playerArray));
        PLAYERS.removeIf(""::equals);
    }

    public static void setPlayers(String[] newPlayers) {
        PLAYERS = List.of(newPlayers);

        String players = String.join(",", PLAYERS);

        FileTools.writeFile(DiscordBot.PLAYERS_FILE, players);
    }

    public static void addPlayer(String name) {
        loadPlayers();

        if(PLAYERS.contains(name)) return;

        PLAYERS.add(name);

        String players = String.join(",", PLAYERS);

        FileTools.writeFile(DiscordBot.PLAYERS_FILE, players);
    }

    public static void removePlayer(String name) {
        loadPlayers();

        if(!PLAYERS.contains(name)) return;

        PLAYERS.removeIf(name::equals);

        String players = String.join(",", PLAYERS);

        FileTools.writeFile(DiscordBot.PLAYERS_FILE, players);
    }

    public static String findConfigOption(String config, String option, boolean multiline, boolean allowEmpty) {
        return findConfigOption(config, option, multiline, allowEmpty, false);
    }

    public static String findConfigOption(String config, String option, boolean multiline, boolean allowEmpty, boolean allowNonexistent) {
        String start = String.format("^%s= *", option);
        String end = (multiline) ? " *;" : " *;|$";
        String output = FormatTools.findStringBetween(config, start, end);
        if((!allowEmpty && output.isEmpty()) || (output == null && !allowNonexistent)) {
            MessageManager.log(String.format("Unable to read config \"%s\". Check for formatting issues. Continuing execution anyways.", option), LogLevel.ERROR);
        }
        if(multiline) output = output.replaceAll("\n", "").replaceAll("\r", "");
        return output;
    }
}
