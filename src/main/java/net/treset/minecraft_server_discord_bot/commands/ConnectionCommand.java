package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.server.CrashHandler;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

public class ConnectionCommand extends Command<CommandConfig.Connection> {
    public ConnectionCommand(Supplier<CommandConfig.Connection> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Connection function) {
        String output;
        String type = Objects.requireNonNull(interaction.getOption("action")).getAsString();
        switch (type) {
            case "status" -> output = getStatus(function);
            case "open" -> output = openConnection(function);
            case "close" -> output = closeConnection(interaction, function);
            default -> output = function.messageUnknownAction.get();
        }

        interaction.getHook().sendMessage(output).queue();
    }

    private static String getStatus(CommandConfig.Connection function) {
        if(ManagementClient.get().isConnected()) {
            return function.messageStatusOpen.get();
        }
        return function.messageStatusClosed.get();
    }

    private static String openConnection(CommandConfig.Connection function) {
        if(ManagementClient.get().isConnected()) {
            return function.messageOpenAlreadyOpen.get();
        }
        try {
            ManagementClient.get().connect();
        } catch (IOException e) {
            return function.messageOpenFailed.get();
        }
        return function.messageOpenSuccess.get();
    }

    private static String closeConnection(SlashCommandInteraction interaction, CommandConfig.Connection function) {
        if(!ManagementClient.get().isConnected()) {
            return function.messageCloseNoConnection.get();
        }

        boolean force = false;
        if(interaction.getOption("force") != null) {
            force = Objects.requireNonNull(interaction.getOption("force")).getAsBoolean();
        }

        CrashHandler.expectStop();
        if(force) {
            ManagementClient.get().forceDisconnect();
            return function.messageCloseForced.get();
        }

        try {
            ManagementClient.get().disconnect();
            return function.messageCloseSuccess.get();
        } catch (IOException e) {
            CrashHandler.unexpectStop();
            return function.messageCloseFailed.get();
        }
    }

    @Override
    public CommandData data() {
        return Commands.slash("connection", "Manage connection to server mod! [Moderator only]")
                .addOptions(new OptionData(OptionType.STRING, "action", "The thing to do.", true)
                        .addChoice("status", "status").addChoice("open", "open").addChoice("close", "close"))
                .addOption(OptionType.BOOLEAN, "force", "Force closing the connection; Does nothing if another action than close is selected", false);
    }
}
