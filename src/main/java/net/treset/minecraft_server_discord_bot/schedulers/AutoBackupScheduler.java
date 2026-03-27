package net.treset.minecraft_server_discord_bot.schedulers;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.logging.OutputConsumer;
import net.treset.minecraft_server_discord_bot.server.BackupHandler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoBackupScheduler {
    private static final DateTimeFormatter backupNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static ScheduledFuture<?> task = null;
    private static boolean eventSinceLastBackup = true;

    public static void scheduleNext(OutputConsumer outputConsumer) {
        if(task != null) task.cancel(false);
        task = null;
        if(Config.get().backup == null || Config.get().backup.auto == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = Config.get().backup.auto.getNext();
        if(next == null) {
            return;
        }

        long until = Duration.between(now, next).getSeconds();
        task = scheduler.schedule(() -> createAuto(outputConsumer), until, TimeUnit.SECONDS);
    }

    public static void createAuto(OutputConsumer outputConsumer) {
        try {
            if (Config.get().backup == null) {
                return;
            }
            if (!Config.get().backup.auto.createIf.shouldCreate(eventSinceLastBackup)) {
                return;
            }
            eventSinceLastBackup = false;

            BackupHandler.Mode mode = Config.get().backup.auto.restartMode.mode();

            BackupHandler.execute(mode, t -> backupNameFormatter.format(t) + "-auto", outputConsumer, false);
        } finally {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.warn(e, "Unable to wait before scheduling next backup");
                }
                scheduleNext(outputConsumer);
            }).start();
        }
    }

    static {
        EventScheduler.onEvent(t -> eventSinceLastBackup = true);
    }
}
