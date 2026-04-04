package net.treset.minecraft_server_discord_bot.commands;

import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.system.Formatter;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class OnlineCommand extends Command<CommandConfig.Online> {
    public OnlineCommand(Supplier<CommandConfig.Online> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Online function) {
        String output;

        List<String> players = getPlayers();
        if(players == null) {
            output = function.messageFailed.get();
            interaction.getHook().sendMessage(output).queue();
            return;
        }

        if (players.isEmpty()) {
            output = function.messageNoPlayers.get();
        } else {
            MessageTemplates.OnlineContext context = new MessageTemplates.OnlineContext(
                    Formatter.formatList(players, "\n"),
                    players.size()
            );
            output = players.size() == 1
                    ? function.messageSinglePlayer.get(context)
                    : function.messageMultiplePlayers.get(context);
        }

        interaction.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }

    private static List<String> getPlayers() {
        try {
            return ManagementClient.get().request(RpcMethods.Players.GET).stream().map(RpcPlayer::name).toList();
        } catch (IOException e) {
            Logger.warn(e,"Failed to get players for players command", e);
            return null;
        }
    }

    @Override
    public CommandData data() {
        return Commands.slash("online", "See who is currently online!");
    }
}
