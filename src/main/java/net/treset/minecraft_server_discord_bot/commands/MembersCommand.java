package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.rpc.MessageHandler;
import net.treset.minecraft_server_discord_bot.rpc.data.RpcPlayer;
import net.treset.minecraft_server_discord_bot.rpc.schemas.RpcResponse;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;
import java.util.List;

public class MembersCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        List<String> members = getMembers();
        if(members == null) {
            output = "Failed to get members!";
            event.getHook().sendMessage(output).queue();
            return;
        }

        output = String.format("Current members are: **%s**.", FormatTools.formatList(members, ", "));
        if(Config.contact.admin != null) {
            output += String.format("\nTo become a member contact **%s**.", Config.contact.admin);
        }
        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }

    private static List<String> getMembers() {
        try {
            RpcResponse res = MessageHandler.sendBlocking("minecraft:allowlist");
            try {
                return RpcPlayer.extractPlayersFromResponse(res);
            } catch (IOException e) {
                MessageManager.log("Failed to extract members for members command", LogLevel.WARN, e);
                return null;
            }
        } catch (IOException e) {
            MessageManager.log("Failed to update members for members command.", LogLevel.WARN, e);
            return null;
        }
    }
}
