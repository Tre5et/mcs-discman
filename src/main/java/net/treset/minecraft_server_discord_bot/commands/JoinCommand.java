package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;

public class JoinCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        ConfigTools.loadDetails();
        String url = ConfigTools.DETAILS.url;

        output = String.format("Join the server with on ip-adress **%s**.\nYou must be member to join. Type ``/members`` for more details.", url);

        event.reply(output).queue();

        DiscordBot.LOGGER.info("Join Command handled.");
    }
}
