package net.treset.minecraft_server_discord_bot.config.function;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class FunctionConfig extends ValidatableConfig {
    public boolean enabled;
    public List<String> allowedRoles;
    public transient List<String> allowedRolesId = null;

    public FunctionConfig(boolean enabled, List<String> allowedRoles) {
        this.enabled = enabled;
        this.allowedRoles = allowedRoles;
    }

    @Override
    public List<String> prefix() {
        return List.of("feature");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        if(enabled && allowedRoles != null) {
            for(String role : allowedRoles) {
                if (!newConfig.discord.roles.containsKey(role)) {
                    throw new ConfigException("Role '" + role + "' is referenced for a function but is not defined in 'discord.roles'.");
                }
            }
            allowedRolesId = allowedRoles.stream().map(r -> newConfig.discord.roles.get(r)).toList();
        }
    }

    public static class EnabledAndAll extends FunctionConfig {
        public EnabledAndAll() {
            super(true, null);
        }
    }

    public static class DisabledAndAll extends FunctionConfig {
        public DisabledAndAll() {
            super(false, null);
        }
    }

    public static class EnabledAndModerator extends FunctionConfig {
        public EnabledAndModerator() {
            super(true, List.of("moderator"));
        }
    }

    public static class DisabledAndModerator extends FunctionConfig {
        public DisabledAndModerator() {
            super(false, List.of("moderator"));
        }
    }
}
