package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.Objects;
import java.util.function.Supplier;

public class AutoBackupCommand extends Command<FunctionConfig.AutoBackup> {
    public AutoBackupCommand(Supplier<FunctionConfig.AutoBackup> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, FunctionConfig.AutoBackup function) {
        String output;

        if(Objects.requireNonNull(event.getOption("state")).getAsBoolean()) {
            if(PermanentOperations.isBackupEnabled) {
                output = function.messageAlreadyEnabled.get();
                event.getHook().sendMessage(output).queue();
                Logger.info("Handled. Already active.");
            } else {
                PermanentOperations.isBackupEnabled = true;
                output = function.messageEnabled.get();
                event.getHook().sendMessage(output).queue();
                Logger.info("Handled. Enabled.");
            }
        } else {
            if(PermanentOperations.isBackupEnabled) {
                PermanentOperations.isBackupEnabled = false;
                output = function.messageDisabled.get();
                event.getHook().sendMessage(output).queue();
                Logger.info("Handled. Disabled.");
            } else {
                output = function.messageAlreadyDisabled.get();
                event.getHook().sendMessage(output).queue();
                Logger.info("Handled. Already inactive.");
            }
        }
    }
}
