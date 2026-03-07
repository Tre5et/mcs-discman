package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ValidatableConfig {
    public abstract List<String> prefix();
    public abstract void validate(Config config) throws ConfigException;

    public void require(Boolean value, String name) throws ConfigException {
        if(value != true) {
            throw new ConfigException("Property '" + String.join(".", prefix()) + "." + name +"' is required but missing.");
        }
    }

    public void require(Number value, String name) throws ConfigException {
        require(value != null && !value.equals(0), name);
    }

    public void require(Collection<?> value, String name) throws ConfigException {
        require(value != null && !value.isEmpty(), name);
    }

    public void require(Object value, String name) throws ConfigException {
        if(value instanceof Boolean) {
            require((Boolean) value, name);
        } else if(value instanceof Number) {
            require((Number) value, name);
        } else if(value instanceof Collection<?>) {
            require((Collection<?>) value, name);
        } else {
            require(value != null, name);
        }
    }

    public void requireAny(ConfigValue... values) throws ConfigException {
        boolean match = Arrays.stream(values).anyMatch(v -> {
            try {
                require(v.value, v.name);
            } catch (ConfigException e) {
                return false;
            }
            return true;
        });
        if(!match) {
            throw new ConfigException("At least one of " + valueNames(values) + " is required but none is set.");
        }
    }

    public void requireExactly(int number, ConfigValue... values) throws ConfigException {
        long count = Arrays.stream(values).filter(v -> {
            try {
                require(v.value, v.name);
            } catch (ConfigException e) {
                return false;
            }
            return true;
        }).count();

        if(count != number) {
            throw new ConfigException("Exactly " + number + " of " + valueNames(values) + " is required, but " + count + " were set");
        }
    }

    public boolean requireAllOrNone(ConfigValue... values) throws ConfigException {
        long count = Arrays.stream(values).filter(v -> {
            try {
                require(v.value, v.name);
            } catch (ConfigException e) {
                return false;
            }
            return true;
        }).count();

        if(count != 0 && count != values.length) {
            throw new ConfigException("Properties " + valueNames(values) + " must be either all set or none set, but " + count + " were set");
        }
        return count != 0;
    }

    private String valueNames(ConfigValue... values) {
        return Arrays.stream(values).map(v -> "'" + String.join(".", prefix()) + "." + v.name + "'").collect(Collectors.joining(", "));
    }

    public static ConfigValue p(Object value, String name) {
        return new ConfigValue(value, name);
    }

    public record ConfigValue(
            Object value,
            String name
    ) {}
 }
