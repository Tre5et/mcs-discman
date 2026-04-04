package net.treset.minecraft_server_discord_bot.commands;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcVersion;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;

import java.util.function.Supplier;

public class DetailsCommand extends Command<CommandConfig.Details> {
    public DetailsCommand(Supplier<CommandConfig.Details> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Details function) {
        String output;

        String version = getVersion();
        if(version == null) {
            output = function.messageFailed.get();
            interaction.getHook().sendMessage(output).queue();
            return;
        }

        output = function.messageVersion.get(new MessageTemplates.DetailsContext(version));
        interaction.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }

    private static String getVersion() {
        try {
            RpcVersion res = ManagementClient.get().request(RpcMethods.Server.STATUS).version();
            return res == null ? null : res.name();
        } catch (RpcCommunicationException e) {
            Logger.warn(e, "Failed to get version for details command", e);
            return null;
        }
    }

    @Override
    public CommandData data() {
        return Commands.slash("details", "See details about the server!");
    }
}
