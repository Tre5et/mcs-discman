package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.DiscmanRpcMethods;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.data.RpcCommand;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

public class RunCommandCommand extends Command<CommandConfig.RunCommand> {
    public RunCommandCommand(Supplier<CommandConfig.RunCommand> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.RunCommand function) {
        String cmd = Objects.requireNonNull(event.getOption("command")).getAsString();

        try {
            RpcCommand res = ManagementClient.get().request(DiscmanRpcMethods.Server.COMMAND_RUN, cmd);
            MessageTemplates.RunCommandContext context = new MessageTemplates.RunCommandContext(cmd, res.message().literal());
            switch (res.status()) {
                case SUCCESS -> {
                    Logger.info("Handled. Success: \"%s\" -> \"%s\"", cmd, res.message().literal());
                    event.getHook().sendMessage(function.messageSuccess.get(context)).queue();
                }
                case FAILURE -> {
                    Logger.info("Handled. Failure: \"%s\" -> \"%s\"", cmd, res.message().literal());
                    event.getHook().sendMessage(function.messageInvalid.get(context)).queue();
                }
                case NO_RESPONSE -> {
                    Logger.info("Handled. Unknown: \"%s\" -> \"%s\"", cmd, res.message().literal());
                    event.getHook().sendMessage(function.messageNoResponse.get(context)).queue();
                }
                default -> {
                    Logger.warn("Failed to parse command result: \"%s\" -> \"s\"", cmd, res);
                    event.getHook().sendMessage(function.messageFailed.get()).queue();
                }
            }
        } catch (IOException e) {
            Logger.warn(e, "Failed to request command execution: \"%s\"", cmd);
            event.getHook().sendMessage(function.messageRequestFailed.get()).queue();
        }
    }

    @Override
    public CommandData data() {
        return new CommandData("runcommand", "Run a command on the server! [Moderator only]")
                .addOption(OptionType.STRING, "command", "The command to run.", true);
    }
}