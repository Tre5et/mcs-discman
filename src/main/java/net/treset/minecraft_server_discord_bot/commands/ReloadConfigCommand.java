package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.logging.Logger;

public class ReloadConfigCommand {
    public static void handleCommand(SlashCommandEvent event) {
        if(DiscordBot.isModerator(event)) {
            try {
                Config.load();
                event.getHook().sendMessage("Reloaded config.").queue();
            } catch (ConfigException e) {
                event.getHook().sendMessage("Failed to reload config.").queue();
            }
        } else {
            event.getHook().sendMessage("You don't have permission to do that.").queue();
            Logger.info("Handled.");
        }
    }
}
