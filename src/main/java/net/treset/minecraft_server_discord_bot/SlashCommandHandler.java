package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.treset.minecraft_server_discord_bot.commands.Commands;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SlashCommandHandler extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if(!event.getChannel().getId().equals(Config.get().discord.messageChannelId)) return;

        event.deferReply().queue();

        switch (event.getName()) {
            case "active": CompletableFuture.runAsync(() -> Commands.ACTIVE.handle(event)); break;
            case "autobackup": CompletableFuture.runAsync(() -> Commands.AUTOBACKUP.handle(event)); break;
            case "backups": CompletableFuture.runAsync(() -> Commands.BACKUPS.handle(event)); break;
            case "connection" : CompletableFuture.runAsync(() -> Commands.CONNECTION.handle(event)); break;
            case "createbackup": CompletableFuture.runAsync(() -> Commands.CREATE_BACKUP.handle(event)); break;
            case "details": CompletableFuture.runAsync(() -> Commands.DETAILS.handle(event)); break;
            case "join": CompletableFuture.runAsync(() -> Commands.JOIN.handle(event));break;
            case "members": CompletableFuture.runAsync(() -> Commands.MEMBERS.handle(event)); break;
            case "online": CompletableFuture.runAsync(() -> Commands.ONLINE.handle(event)); break;
            case "ping": CompletableFuture.runAsync(() -> Commands.PING.handle(event)); break;
            case "restartserver": CompletableFuture.runAsync(() -> Commands.RESTART.handle(event)); break;
            case "say": CompletableFuture.runAsync(() -> Commands.SAY.handle(event)); break;
            case "startserver": CompletableFuture.runAsync(() -> Commands.START.handle(event)); break;
            case "stopserver": CompletableFuture.runAsync(() -> Commands.STOP.handle(event)); break;
            case "runcommand": CompletableFuture.runAsync(() -> Commands.RUN_COMMAND.handle(event)); break;
            case "reloadconfig": CompletableFuture.runAsync(() -> Commands.RELOAD.handle(event)); break;
            default:
                event.getHook().sendMessage("Sorry, I don't know that :worried:").queue();
                Logger.warn("Unable to handle command \"%s\". Unknown.", event.getName());
                break;
        }
    }
}