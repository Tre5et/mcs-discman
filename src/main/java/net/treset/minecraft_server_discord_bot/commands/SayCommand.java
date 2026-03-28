package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.Objects;
import java.util.function.Supplier;

public class SayCommand extends Command<FunctionConfig.Say> {
    public SayCommand(Supplier<FunctionConfig.Say> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, FunctionConfig.Say function) {
        String message = Objects.requireNonNull(event.getOption("message")).getAsString();
        event.getHook().sendMessage(message).queue();

        Logger.info("Handled. Said \"%s\".", message);
    }
}
