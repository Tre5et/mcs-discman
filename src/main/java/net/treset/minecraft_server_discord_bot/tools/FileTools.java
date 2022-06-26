package net.treset.minecraft_server_discord_bot.tools;

import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileTools {
    public static String readFile(String path) {
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            String data = "";
            while (myReader.hasNextLine()) {
                data += myReader.nextLine() + "\n";
            }
            myReader.close();
            return data;
        } catch (FileNotFoundException e) {
            MessageManager.log(String.format("Unable to read file %s! -> Stacktrace.", path), LogLevel.ERROR);
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static void writeFile(String path, String data) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            MessageManager.log(String.format("Unable to write %s to %s! -> Stacktrace.", data, path), LogLevel.ERROR);
            e.printStackTrace();
        }
    }

    public static boolean zipFile(String sourceDirPath, String zipFilePath) {
        AtomicBoolean operationSuccess = new AtomicBoolean(false);

        Path p = null;
        try {
            p = Files.createFile(Paths.get(zipFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                            operationSuccess.set(true);
                        } catch (IOException e) {
                            System.err.println(e);
                            operationSuccess.set(false);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if(!operationSuccess.get()) {
            return false;
        }
        return true;
    }

}
