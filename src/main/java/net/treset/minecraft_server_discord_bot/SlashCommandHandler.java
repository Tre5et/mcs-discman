package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.treset.minecraft_server_discord_bot.commands.*;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SlashCommandHandler extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if(!event.getChannel().getId().equals(ConfigTools.CONFIG.MESSAGE_CHANNEL_ID)) return;

        event.deferReply().queue();

        switch (event.getName()) {
            case "active": CompletableFuture.runAsync(() -> ActiveCommand.handleCommand(event)); break;
            case "autobackup": CompletableFuture.runAsync(() -> AutoBackupCommand.handleCommand(event)); break;
            case "backups": CompletableFuture.runAsync(() -> BackupsCommand.handleCommand(event)); break;
            case "connection" : CompletableFuture.runAsync(() -> ConnectionCommand.handleCommand(event)); break;
            case "createbackup": CompletableFuture.runAsync(() -> CreateBackupCommand.handleCommand(event)); break;
            case "details": CompletableFuture.runAsync(() -> DetailsCommand.handleCommand(event)); break;
            case "ingame" : CompletableFuture.runAsync(() -> IngameCommand.handleCommand(event)); break;
            case "join": CompletableFuture.runAsync(() -> JoinCommand.handleCommand(event));break;
            case "members": CompletableFuture.runAsync(() -> MembersCommand.handleCommand(event)); break;
            case "logconsole":  CompletableFuture.runAsync(() ->LogConsoleCommand.handleCommand(event)); break;
            case "online": CompletableFuture.runAsync(() -> OnlineCommand.handleCommand(event)); break;
            case "ping": CompletableFuture.runAsync(() -> PingCommand.handleCommand(event)); break;
            case "restartserver": CompletableFuture.runAsync(() -> RestartServerCommand.handleCommand(event)); break;
            case "runcommand": CompletableFuture.runAsync(() -> RunCommandCommand.handleCommand(event)); break;
            case "say": CompletableFuture.runAsync(() -> SayCommand.handleCommand(event)); break;
            case "startserver": CompletableFuture.runAsync(() -> StartServerCommand.handleCommand(event)); break;
            case "stopserver": CompletableFuture.runAsync(() -> StopServerCommand.handleCommand(event)); break;
            default:
                event.getHook().sendMessage("Sorry, I don't know that :worried:").queue();
                MessageManager.log(String.format("Unable to handle command \"%s\". Unknown.", event.getName()), LogLevel.WARN);
                break;
        }
    }
}