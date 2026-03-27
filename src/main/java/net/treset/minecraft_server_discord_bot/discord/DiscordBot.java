package net.treset.minecraft_server_discord_bot.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.treset.minecraft_server_discord_bot.SlashCommandHandler;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Objects;

public class DiscordBot {
    public static JDA JDA;
    public static Guild GUILD;
    public static MessageChannel BOT_CHANNEL;
    public static Role MODERATOR_ROLE;

    public static void initialize() throws IOException {
        try {
            initClient();
        } catch (LoginException | InterruptedException e) {
            throw new IOException("Failed to initialize Discord Bot", e);
        }
        upsertCommands();
    }

    public static void initClient() throws LoginException, InterruptedException {
        JDA = JDABuilder.createDefault(Config.get().discord.token)
                .addEventListeners(new SlashCommandHandler())
                .build();
        JDA.awaitReady();

        GUILD = JDA.getGuildById(Config.get().discord.guildId);
        BOT_CHANNEL = JDA.getTextChannelById(Config.get().discord.messageChannelId);
        MODERATOR_ROLE = JDA.getRoleById(Config.get().discord.roles.get("moderator"));

        Logger.info("Client initialized.");
    }

    public static void upsertCommands() {
        GUILD.upsertCommand("active", "Check if the server is running!").queue();
        GUILD.upsertCommand("autobackup", "Toggle auto-backup! [Moderator only]")
                .addOption(OptionType.BOOLEAN, "state", "The state the auto-backup should be in.", true).queue();
        GUILD.upsertCommand("backups", "See where to find backups!").queue();
        GUILD.upsertCommand("createbackup", "Create a backup! [Moderator only]").addOptions(
                new OptionData(OptionType.STRING, "mode", "The backup mode to use.", true)
                        .addChoice("restart", "restart").addChoice("while running", "while-running"),
                new OptionData(OptionType.BOOLEAN, "skip-notify", "Don't notify the players and create the backup instantly")
        ).queue();
        GUILD.upsertCommand("connection", "Manage connection to server mod! [Moderator only]").addOptions(
                new OptionData(OptionType.STRING, "action", "The thing to do.", true)
                        .addChoice("status", "status").addChoice("open", "open").addChoice("close", "close")
        ).addOption(OptionType.BOOLEAN, "force", "Force closing the connection; Does nothing if another action than close is selected", false).queue();
        GUILD.upsertCommand("details", "See details about the server!").queue();
        GUILD.upsertCommand("ingame", "Do or get InGame stuff!").addOptions(
                new OptionData(OptionType.STRING, "action", "The thing that will be done.", true)
                        .addChoice("get time", "time")
        ).queue();
        GUILD.upsertCommand("join", "See how to join the server!").queue();
        GUILD.upsertCommand("members", "See the current members of the server!").queue();
        GUILD.upsertCommand("logconsole", "Toggle full console logging!").addOption(OptionType.BOOLEAN, "state", "The state the logging should be in.", true).queue();
        GUILD.upsertCommand("online", "See who is currently online!").queue();
        GUILD.upsertCommand("ping", "See if the bot is online!").queue();
        GUILD.upsertCommand("restartserver", "Restart the server! [Moderator only]").queue();
        GUILD.upsertCommand("runcommand", "Run a command on the server! [Moderator only]")
                .addOption(OptionType.STRING, "command", "The command to be run.", true).queue();
        GUILD.upsertCommand("say", "Make the bot say something! [Moderator only]")
                .addOption(OptionType.STRING, "message", "The message the bot will say.", true).queue();
        GUILD.upsertCommand("startserver", "Start the server! [Moderator only]").queue();
        GUILD.upsertCommand("stopserver", "Start the server! [Moderator only]").queue();
        GUILD.upsertCommand("runcommand", "Run a command on the server! [Moderator only]")
                .addOption(OptionType.STRING, "command", "The command to run.", true).queue();
        GUILD.upsertCommand("reloadconfig", "Reloads the configuration from a file! [Moderator only]").queue();

        Logger.info("Commands enabled.");
    }

    public static boolean isModerator(SlashCommandEvent event) {
        return Objects.requireNonNull(event.getMember()).getRoles().contains(MODERATOR_ROLE);
    }

    private static void sendMessageToDiscord(String message) {
        if(BOT_CHANNEL == null) return;
        BOT_CHANNEL.sendMessage(message).queue();
    }

    public static void sendText(String text, MessageOrigin org) {
        sendMessageToDiscord(text);

        Logger.info("Sent message \"%s\" %s.", text, org.getMessage());
    }
}
