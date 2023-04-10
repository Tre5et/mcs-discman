package net.treset.minecraft_server_discord_bot.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataTools {
    public static List<String> loadWhitelist() {
        return loadPlayersFromFile(ConfigTools.CONFIG.SERVER_PATH + "whitelist.json");
    }

    public static List<String> loadOps() {
        return loadPlayersFromFile(ConfigTools.CONFIG.SERVER_PATH + "ops.json");
    }

    public static List<String> loadPlayersFromFile(String filePath) {
        if(!FileTools.fileExists(filePath)) {
            return new ArrayList<>();
        }

        String playerListJson = FileTools.readFile(filePath);
        JsonElement playerList = FormatTools.parseJson(playerListJson);
        List<String> players = new ArrayList<>();
        if(playerList.isJsonArray()) {
            JsonArray whitelistArray = playerList.getAsJsonArray();
            for(JsonElement p : whitelistArray) {
                if(p.isJsonObject()) {
                    JsonObject pObject = p.getAsJsonObject();
                    if (pObject.get("name").isJsonPrimitive()) {
                        JsonPrimitive pName = pObject.getAsJsonPrimitive("name");
                        if(pName.isString()) {
                            players.add(pName.getAsString());
                        }
                    }
                }
            }
        }
        return players;
    }

    public static ServerType loadServerType() {
        File[] jars = FileTools.findFilesMatching(".*\\.jar", ConfigTools.CONFIG.SERVER_PATH);
        if(jars.length == 1) {
            if(jars[0].getName().equals("server.jar")) {
                return ServerType.VANILLA;
            }
            if(FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "mods") || FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "config")) {
                return ServerType.MODDED;
            }
            return ServerType.VANILLA_L;
        }
        if(FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "libraries/net/fabricmc")) {
            if(FileTools.findFilesMatching(".*fabric.*", ConfigTools.CONFIG.SERVER_PATH).length > 0) {
                return ServerType.FABRIC;
            }
            return ServerType.FABRIC_L;
        }
        if(FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "libraries/net/minecraftforge")) {
            if(jars.length == 0) {
                return ServerType.FORGE;
            }
            return ServerType.FORGE_L;
        }
        if(jars.length >= 2 || FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "mods") || FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "config")) {
            return ServerType.MODDED;
        }
        return ServerType.UNKNOWN;
    }

    public static String loadVersion() {
        String versionJson = FileTools.getFileFromZip(ConfigTools.CONFIG.SERVER_PATH + "server.jar", "version.json");

        if(versionJson.isBlank()) {
            return "??";
        }

        JsonElement version = FormatTools.parseJson(versionJson.toString());
        if(version != null && version.isJsonObject()) {
            if(version.getAsJsonObject().get("name").isJsonPrimitive()) {
                JsonPrimitive versionName = version.getAsJsonObject().getAsJsonPrimitive("name");
                if(versionName.isString()) {
                    return versionName.getAsString();
                }
            }
        }

        return "??";
    }

    public static List<String> loadFabricMods() {
        if(!FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "mods")) {
            return new ArrayList<>();
        }

        File[] mods = FileTools.findFilesMatching(".*\\.jar", ConfigTools.CONFIG.SERVER_PATH + "mods");

        List<String> modList = new ArrayList<>();

        for(File f : mods) {
            String fabricModJson = FileTools.getFileFromZip(f.getPath(), "fabric.mod.json");
            if(!fabricModJson.isBlank()) {
                JsonElement fabricMod = FormatTools.parseJson(fabricModJson);
                if(fabricMod.isJsonObject()) {
                    if(fabricMod.getAsJsonObject().get("name").isJsonPrimitive()) {
                        JsonPrimitive modName = fabricMod.getAsJsonObject().getAsJsonPrimitive("name");
                        if(modName.isString()) {
                            modList.add(modName.getAsString());
                        }
                    } else if(fabricMod.getAsJsonObject().get("id").isJsonPrimitive()) {
                        JsonPrimitive modId = fabricMod.getAsJsonObject().getAsJsonPrimitive("id");
                        if(modId.isString()) {
                            modList.add(modId.getAsString());
                        }
                    }
                }
            }
        }
        return modList;
    }
}
