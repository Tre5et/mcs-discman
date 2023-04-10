package net.treset.minecraft_server_discord_bot.tools;

public enum ServerType {
    VANILLA(true, "Vanilla"),
    VANILLA_L(false, "likely vanilla"),
    FABRIC(true, "Fabric Loader"),
    FABRIC_L(false, "likely Fabric Loader"),
    FORGE(true, "Forge"),
    FORGE_L(false, "likely Forge"),
    MODDED(false, "likely Modded"),
    UNKNOWN(false, "unknown");

    private boolean certain;
    private String displayName;

    ServerType(boolean certain, String displayName) {
        this.certain = true;
        this.displayName = displayName;
    }

    public boolean isCertain() {
        return certain;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return this.getDisplayName();
    }
}
