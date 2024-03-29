package net.treset.minecraft_server_discord_bot.tools;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.SlashCommandHandler;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public class DiscordTools {
    public static void initClient() throws LoginException, InterruptedException {
        DiscordBot.JDA = JDABuilder.createDefault(ConfigTools.CONFIG.TOKEN)
                .addEventListeners(new SlashCommandHandler())
                .build();


        DiscordBot.JDA.awaitReady();

        DiscordBot.GUILD = DiscordBot.JDA.getGuildById(ConfigTools.CONFIG.GUILD_ID);
        DiscordBot.BOT_CHANNEL = DiscordBot.JDA.getTextChannelById(ConfigTools.CONFIG.MESSAGE_CHANNEL_ID);
        DiscordBot.MODERATOR_ROLE = DiscordBot.JDA.getRoleById(ConfigTools.CONFIG.MODERATOR_ID);

        MessageManager.log("Client initialized.", LogLevel.INFO);
    }

    public static void upsertCommands() {
        DiscordBot.GUILD.upsertCommand("active", "Check if the server is running!").queue();
        DiscordBot.GUILD.upsertCommand("autobackup", "Toggle auto-backup! [Moderator only]")
                .addOption(OptionType.BOOLEAN, "state", "The state the auto-backup should be in.", true).queue();
        DiscordBot.GUILD.upsertCommand("backups", "See where to find backups!").queue();
        DiscordBot.GUILD.upsertCommand("createbackup", "Create a backup! [Moderator only]").queue();
        DiscordBot.GUILD.upsertCommand("connection", "Manage connection to server mod! [Moderator only]").addOptions(
                new OptionData(OptionType.STRING, "action", "The thing to do.", true)
                        .addChoice("status", "status").addChoice("open", "open").addChoice("close", "close")
            ).addOption(OptionType.BOOLEAN, "force", "Force closing the connection; Does nothing if another action than close is selected", false).queue();
        DiscordBot.GUILD.upsertCommand("details", "See details about the server!").queue();
        DiscordBot.GUILD.upsertCommand("ingame", "Do or get InGame stuff!").addOptions(
                new OptionData(OptionType.STRING, "action", "The thing that will be done.", true)
                        .addChoice("get time", "time")
        ).queue();
        DiscordBot.GUILD.upsertCommand("join", "See how to join the server!").queue();
        DiscordBot.GUILD.upsertCommand("members", "See the current members of the server!").queue();
        DiscordBot.GUILD.upsertCommand("logconsole", "Toggle full console logging!").addOption(OptionType.BOOLEAN, "state", "The state the logging should be in.", true).queue();
        DiscordBot.GUILD.upsertCommand("online", "See who is currently online!").queue();
        DiscordBot.GUILD.upsertCommand("ping", "See if the bot is online!").queue();
        DiscordBot.GUILD.upsertCommand("restartserver", "Restart the server! [Moderator only]").queue();
        DiscordBot.GUILD.upsertCommand("runcommand", "Run a command on the server! [Moderator only]")
                .addOption(OptionType.STRING, "command", "The command to be run.", true).queue();
        DiscordBot.GUILD.upsertCommand("say", "Make the bot say something! [Moderator only]")
                .addOption(OptionType.STRING, "message", "The message the bot will say.", true).queue();
        DiscordBot.GUILD.upsertCommand("startserver", "Start the server! [Moderator only]").queue();
        DiscordBot.GUILD.upsertCommand("stopserver", "Start the server! [Moderator only]").queue();

        MessageManager.log("Commands enabled.", LogLevel.INFO);
    }

    public static boolean isModerator(SlashCommandEvent event) {
        return Objects.requireNonNull(event.getMember()).getRoles().contains(DiscordBot.MODERATOR_ROLE);
    }
}
