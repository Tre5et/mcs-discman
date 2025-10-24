package net.treset.minecraft_server_discord_bot.commands;

import dev.treset.mcdl.servermanagement.request.RpcResponse;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.data.RpcPlayer;
import net.treset.minecraft_server_discord_bot.system.Formatter;

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

        output = members.isEmpty() ? "There are no current members." : String.format("Current members are: **%s**.", Formatter.formatList(members, ", "));
        if(Config.contact.admin != null) {
            output += String.format("\nTo become a member contact **%s**.", Config.contact.admin);
        }
        event.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }

    private static List<String> getMembers() {
        try {
            RpcResponse res = ManagementClient.get().request("minecraft:allowlist");
            try {
                return RpcPlayer.fromList(res).stream().map(RpcPlayer::getName).toList();
            } catch (IOException e) {
                Logger.warn(e, "Failed to extract members for members command");
                return null;
            }
        } catch (IOException e) {
            Logger.warn(e, "Failed to update members for members command.");
            return null;
        }
    }
}
