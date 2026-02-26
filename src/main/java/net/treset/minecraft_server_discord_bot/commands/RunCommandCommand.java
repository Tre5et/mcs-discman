package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.DiscmanRpcMethods;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.data.RpcCommand;

import java.io.IOException;
import java.util.Objects;

public class RunCommandCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String cmd = Objects.requireNonNull(event.getOption("command")).getAsString();

        if(DiscordBot.isModerator(event)) {
            try {
                RpcCommand res = ManagementClient.get().request(DiscmanRpcMethods.Server.COMMAND_RUN, cmd);
                switch (res.status()) {
                    case SUCCESS -> {
                        Logger.info("Handled. Success: \"%s\" -> \"%s\"", cmd, res.message().literal());
                        event.getHook().sendMessage(String.format("Success: %s", res.message().literal())).queue();
                    }
                    case FAILURE -> {
                        Logger.info("Handled. Failure: \"%s\" -> \"%s\"", cmd, res.message().literal());
                        event.getHook().sendMessage(String.format("Invalid command: %s", res.message().literal())).queue();
                    }
                    case NO_RESPONSE -> {
                        Logger.info("Handled. Unknown: \"%s\" -> \"%s\"", cmd, res.message().literal());
                        event.getHook().sendMessage(String.format("Ran: %s", res.message().literal())).queue();
                    }
                    default -> {
                        Logger.warn("Failed to parse command result: \"%s\" -> \"s\"", cmd, res);
                        event.getHook().sendMessage("Failed to run command.").queue();
                    }
                }
            } catch (IOException e) {
                Logger.warn(e, "Failed to request command execution: \"%s\"", cmd);
                event.getHook().sendMessage("Failed to request command execution.").queue();
            }
        } else {
            Logger.info("Handled. Permission denied for command \"%s\".", cmd);
            event.getHook().sendMessage("You don't have permission to do that.").queue();
        }
    }
}