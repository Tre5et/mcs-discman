package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

public class ConnectionCommand extends Command<FunctionConfig.Connection> {
    public ConnectionCommand(Supplier<FunctionConfig.Connection> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, FunctionConfig.Connection function) {
        String output;
        String type = Objects.requireNonNull(event.getOption("action")).getAsString();
        switch (type) {
            case "status" -> output = getStatus(function);
            case "open" -> output = openConnection(function);
            case "close" -> output = closeConnection(event, function);
            default -> output = function.messageUnknownAction.get();
        }

        event.getHook().sendMessage(output).queue();
    }

    private static String getStatus(FunctionConfig.Connection function) {
        if(ManagementClient.get().isConnected()) {
            return function.messageStatusOpen.get();
        }
        return function.messageStatusClosed.get();
    }

    private static String openConnection(FunctionConfig.Connection function) {
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

    private static String closeConnection(SlashCommandEvent event, FunctionConfig.Connection function) {
        if(!ManagementClient.get().isConnected()) {
            return function.messageCloseNoConnection.get();
        }

        boolean force = false;
        if(event.getOption("force") != null) {
            force = Objects.requireNonNull(event.getOption("force")).getAsBoolean();
        }

        if(force) {
            ManagementClient.get().forceDisconnect();
            return function.messageCloseForced.get();
        }

        try {
            ManagementClient.get().disconnect();
            return function.messageCloseSuccess.get();
        } catch (IOException e) {
            return function.messageCloseFailed.get();
        }
    }
}
