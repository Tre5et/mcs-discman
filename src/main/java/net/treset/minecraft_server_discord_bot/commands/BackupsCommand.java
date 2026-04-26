package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageContext;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.function.Supplier;

public class BackupsCommand extends Command<CommandConfig.Backups> {
    public BackupsCommand(Supplier<CommandConfig.Backups> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Backups function) {
        String output;
        MessageTemplates.BackupsContext context = new MessageTemplates.BackupsContext(
                Config.get().backup.publicLocation,
                Config.get().discord.admin
        );

        if(Config.get().backup.publicLocation != null) {
            if(Config.get().discord.admin != null) {
                output = function.messageLocationAndAdmin.get(context, MessageContext.DISCORD);
            } else {
                output = function.messageLocation.get(context, MessageContext.DISCORD);
            }
        } else if(Config.get().discord.admin != null) {
            output = function.messageAdmin.get(context, MessageContext.DISCORD);
        } else {
            output = function.messageNone.get(MessageContext.DISCORD);
        }

        interaction.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }

    @Override
    public CommandData data() {
        return Commands.slash("backups", "See where to find backups!");
    }
}
