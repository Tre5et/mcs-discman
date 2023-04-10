package net.treset.minecraft_server_discord_bot.tools;

import com.google.api.client.util.IOUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.io.DiscordbotConfig;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

        String whitelistJson = FileTools.readFile(filePath);
        JsonObject whitelist = FormatTools.parseJson(whitelistJson);
        List<String> whitelistPlayers = new ArrayList<>();
        if(whitelist.isJsonArray()) {
            JsonArray whitelistArray = whitelist.getAsJsonArray();
            for(JsonElement p : whitelistArray) {
                if(p.isJsonObject()) {
                    JsonObject pObject = p.getAsJsonObject();
                    if (pObject.get("name").isJsonPrimitive()) {
                        JsonPrimitive pName = pObject.getAsJsonPrimitive("name");
                        if(pName.isString()) {
                            whitelistPlayers.add(pName.getAsString());
                        }
                    }
                }
            }
        }
        return whitelistPlayers;
    }

    public static ServerType loadServerType() {
        File[] jars = FileTools.findFilesMatching(".*\\.jar", ConfigTools.CONFIG.SERVER_PATH);
        if(jars.length == 1) {
            if(jars[0].getName().equals("server.jar")) {
                return ServerType.VANILLA;
            } else return ServerType.VANILLA_L;
        }
        if(FileTools.findFilesMatching(".*fabric.*", ConfigTools.CONFIG.SERVER_PATH).length > 0) {
            if(FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "libraries/net/fabricmc")) {
                return ServerType.FABRIC;
            }
            return ServerType.FABRIC_L;
        }
        if(jars.length == 0 && FileTools.dirExists(ConfigTools.CONFIG.SERVER_PATH + "libraries/net/minecraftforge")) {
            return ServerType.FORGE;
        }
        if(jars.length >= 2) {
            return ServerType.MODDED;
        }
        return ServerType.UNKNOWN;
    }

    public static String loadVersion() {
        if(FileTools.fileExists(ConfigTools.CONFIG.SERVER_PATH + "server.jar")) {
            FileTools.createDir("./temp");

            try {
                ZipFile zipFile = new ZipFile(ConfigTools.CONFIG.SERVER_PATH + "server.jar");

                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while(entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if(!entry.isDirectory() && entry.getName().equals("version.json")) {
                        InputStream stream = zipFile.getInputStream(entry);
                        StringBuilder versionJson = new StringBuilder();
                        try (Reader reader = new BufferedReader(new InputStreamReader
                                (stream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                            int c = 0;
                            while ((c = reader.read()) != -1) {
                                versionJson.append((char) c);
                            }
                        }

                        JsonObject version = FormatTools.parseJson(versionJson.toString());
                        if(version != null && version.isJsonObject()) {
                            if(version.get("name").isJsonPrimitive()) {
                                JsonPrimitive versionName = version.getAsJsonPrimitive("name");
                                if(versionName.isString()) {
                                    return versionName.getAsString();
                                }
                            }
                        }

                        return "??";
                    }
                }

                zipFile.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        return "??";
    }
}
