package net.treset.minecraft_server_discord_bot.config.backup;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.server.BackupHandler;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.function.Function;

public class AutoBackupConfig extends ValidatableConfig {
    public List<Integer> hour = List.of(0);
    public List<Integer> minute = List.of(0);
    public List<DayOfWeek> day = List.of();
    public List<Integer> dayOfMonth = List.of();
    public List<Integer> dayOfYear = List.of();
    public int dayInterval;
    public RestartMode restartMode = RestartMode.never;
    public BackupCondition createIf = BackupCondition.event;

    public LocalDateTime getNext() {
        LocalDateTime time = LocalDateTime.now().plusMinutes(1);
        int i = 0;
        while(!minute.contains(time.getMinute())) {
            time = time.plusMinutes(1);
            if(i++ > 59) {
                return null;
            }
        }
        i = 0;
        while(!hour.contains(time.getHour())) {
            time = time.plusHours(1);
            if(i++ > 23) {
                return null;
            }
        }
        i = 0;
        while(!isBackupDay(time)) {
            time = time.plusDays(1);
            if(i++ > 364) {
                return null;
            }
        }
        return time.withSecond(0).withNano(0);
    }

    public boolean isBackupDay(LocalDateTime date) {
        return
                (day.isEmpty() || day.contains(date.getDayOfWeek()))
                        && (dayOfMonth.isEmpty() || dayOfMonth.contains(date.getDayOfMonth()))
                        && (dayOfYear.isEmpty() || dayOfYear.contains(date.getDayOfYear()))
                        && (dayInterval <= 0 || Duration.between(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), date).toDays() % dayInterval == 0);
    }

    @Override
    public List<String> prefix() {
        return List.of("backup", "auto");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {

    }

    public enum RestartMode {
        always(p -> BackupHandler.Mode.RESTART),
        never(p -> BackupHandler.Mode.WHILE_RUNNING),
        empty(p -> p ? BackupHandler.Mode.WHILE_RUNNING : BackupHandler.Mode.RESTART);

        private final Function<Boolean, BackupHandler.Mode> modeConverter;

        RestartMode(Function<Boolean, BackupHandler.Mode> modeConverter) {
            this.modeConverter = modeConverter;
        }

        public BackupHandler.Mode mode() {
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

            return modeConverter.apply(playersOnline);
        }
    }

    public enum BackupCondition {
        always(e -> true),
        never(e -> false),
        event(e -> e);

        private final Function<Boolean, Boolean> creationDecider;

        BackupCondition(Function<Boolean, Boolean> creationDecider) {
            this.creationDecider = creationDecider;
        }

        public boolean shouldCreate(boolean eventHappened) {
            return creationDecider.apply(eventHappened);
        }
    }
}
