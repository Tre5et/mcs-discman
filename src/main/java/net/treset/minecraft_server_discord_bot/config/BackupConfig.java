package net.treset.minecraft_server_discord_bot.config;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.server.BackupHandler;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.ServerActions;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.time.Duration;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.BiFunction;

public class BackupConfig extends Validatable {
    public String path;
    public List<Integer> notifyAt = List.of(300, 60, 30, 15, 10, 5, 4, 3, 2, 1);
    public NotifyCondition notifyIf = NotifyCondition.restartAndNotEmpty;
    public String publicLocation;
    public AutoBackupConfig auto;
    public UploadConfig upload;

    public List<Duration> notificationOffsets() {
        return notifyAt.stream().map(s -> Duration.of(s, ChronoUnit.SECONDS)).toList();
    }

    public UploadService uploadService() {
        if(upload != null) {
            return upload.service();
        }
        return null;
    }

    @Override
    public List<String> prefix() {
        return List.of("backup");
    }

    @Override
    public void validate() throws ConfigException {
        require(path, "path");
        if(auto != null) {
            auto.validate();
        }
        if(upload != null) {
            upload.validate();
        }
    }

    public enum NotifyCondition {
        always((m,p) -> true),
        notEmpty((m,p) -> p),
        never((m,p) -> false),
        restart((m,p) -> m == BackupHandler.Mode.RESTART),
        restartAndNotEmpty((m,p) -> m == BackupHandler.Mode.RESTART && p),
        notRestart((m,p) -> m == BackupHandler.Mode.WHILE_RUNNING),
        notRestartAndNotEmpty((m,p) -> m == BackupHandler.Mode.WHILE_RUNNING && p);

        private final BiFunction<BackupHandler.Mode, Boolean, Boolean> notifyDecider;

        NotifyCondition(BiFunction<BackupHandler.Mode, Boolean, Boolean> notifyDecider) {
            this.notifyDecider = notifyDecider;
        }

        public boolean shouldNotify(BackupHandler.Mode mode) {
            boolean playersOnline = false;
            if(ServerActions.isRunning()) {
                try {
                    playersOnline = !ManagementClient.get().request(
                            RpcMethods.Players.GET
                    ).isEmpty();
                } catch (RpcCommunicationException e) {
                    playersOnline = true;
                }
            }

            return notifyDecider.apply(mode, playersOnline);
        }
    }
}
