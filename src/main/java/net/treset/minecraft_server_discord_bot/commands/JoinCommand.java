package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.logging.Logger;

public class JoinCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        if(Config.contact.url != null) {
            output = String.format("Join the server using the address **%s**. You must be member to join. Type ``/members`` for more details.", Config.contact.url);
        } else {
            output = "No information about how to join the server is configured.";
        }

        event.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }
}
