package net.treset.minecraft_server_discord_bot.logging;

import net.treset.minecraft_server_discord_bot.config.Config;
import org.slf4j.LoggerFactory;

public class Logger {
    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Logger.class);

    public static void debug(String message, Object... arguments) {
        String origin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        log(String.format(message, arguments), LogLevel.DEBUG, null, origin);
    }

    public static void debug(Exception e, String message, Object... arguments) {
        String origin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        log(String.format(message, arguments), LogLevel.DEBUG, e, origin);
    }

    public static void info(String message, Object... arguments) {
        String origin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        log(String.format(message, arguments), LogLevel.INFO, null, origin);
    }

    public static void info(Exception e, String message, Object... arguments) {
        String origin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        log(String.format(message, arguments), LogLevel.INFO, e, origin);
    }

    public static void warn(String message, Object... arguments) {
        String origin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        log(String.format(message, arguments), LogLevel.WARN, null, origin);
    }

    public static void warn(Exception e, String message, Object... arguments) {
        String origin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        log(String.format(message, arguments), LogLevel.WARN, e, origin);
    }

    public static void error(String message, Object... arguments) {
        String origin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        log(String.format(message, arguments), LogLevel.ERROR, null, origin);
    }

    public static void error(Exception e, String message, Object... arguments) {
        String origin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        log(String.format(message, arguments), LogLevel.ERROR, e, origin);
    }

    private static void log(String message, LogLevel level, Exception e, String origin) {
        String msg = origin + ": " + message;
        switch(level) {
            case DEBUG -> {
                if(Config.discord.debug) {
                    if (e == null) {
                        LOGGER.debug(msg);
                    } else {
                        LOGGER.debug(msg, e);
                    }
                }
            }
            case INFO -> {
                if (e == null) {
                    LOGGER.info(msg);
                } else {
                    LOGGER.info(msg, e);
                }
            }
            case WARN -> {
                if (e == null) {
                    LOGGER.warn(msg);
                } else {
                    LOGGER.warn(msg, e);
                }
            }
            case ERROR -> {
                if (e == null) {
                    LOGGER.error(msg);
                } else {
                    LOGGER.error(msg, e);
                }
            }
        }
    }
}
