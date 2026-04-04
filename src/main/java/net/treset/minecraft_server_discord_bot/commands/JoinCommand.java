package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.function.Supplier;

public class JoinCommand extends Command<CommandConfig.Join> {
    public JoinCommand(Supplier<CommandConfig.Join> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Join function) {
        String output;

        if(Config.get().server.url != null) {
            output = function.messageJoin.get(new MessageTemplates.JoinContext(Config.get().server.url));
        } else {
            output = function.messageMissingInfo.get();
        }

        interaction.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }

    @Override
    public CommandData data() {
        return Commands.slash("join", "See how to join the server!");
    }
}
