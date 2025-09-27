package net.treset.minecraft_server_discord_bot.config;

public class ContactConfig extends BaseConfig {
    public final String url;
    public final String admin;
    public final String backups_location;

    public ContactConfig(String config) {
        super(config);

        url = load("url");
        admin = load("admin");
        backups_location = load("backups_location");
    }
}
