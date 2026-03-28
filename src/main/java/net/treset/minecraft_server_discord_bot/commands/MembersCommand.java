package net.treset.minecraft_server_discord_bot.commands;

import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.system.Formatter;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class MembersCommand extends Command<FunctionConfig.Members> {
    public MembersCommand(Supplier<FunctionConfig.Members> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, FunctionConfig.Members function) {
        String output;

        List<String> members = getMembers();
        if(members == null) {
            output = function.messageFailed.get();
            event.getHook().sendMessage(output).queue();
            return;
        }

        String memberList = Formatter.formatList(members, ", ");
        MessageTemplates.MembersContext context = new MessageTemplates.MembersContext(memberList, Config.get().discord.admin, members.size());
        output = members.isEmpty() ? function.messageNoMembers.get() : function.messageMembers.get(context);
        if(Config.get().discord.admin != null) {
            output += "\n" + function.messageContactAdmin.get(context);
        }
        event.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }

    private static List<String> getMembers() {
        try {
            return ManagementClient.get().request(RpcMethods.Allowlist.GET).stream().map(RpcPlayer::name).toList();
        } catch (IOException e) {
            Logger.warn(e, "Failed to get members for members command", e);
            return null;
        }
    }
}
