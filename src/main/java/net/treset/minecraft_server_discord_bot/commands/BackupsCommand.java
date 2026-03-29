package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.function.Supplier;

public class BackupsCommand extends Command<CommandConfig.Backups> {
    public BackupsCommand(Supplier<CommandConfig.Backups> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.Backups function) {
        String output;
        MessageTemplates.BackupsContext context = new MessageTemplates.BackupsContext(
                Config.get().backup.publicLocation,
                Config.get().discord.admin
        );

        if(Config.get().backup.publicLocation != null) {
            if(Config.get().discord.admin != null) {
                output = function.messageLocationAndAdmin.get(context);
            } else {
                output = function.messageLocation.get(context);
            }
        } else if(Config.get().discord.admin != null) {
            output = function.messageAdmin.get(context);
        } else {
            output = function.messageNone.get();
        }

        event.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }
}
