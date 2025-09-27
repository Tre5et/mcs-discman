package net.treset.minecraft_server_discord_bot.tools;

import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileTools {
    public static String readFile(String path) throws IOException {
        File myObj = new File(path);
        Scanner myReader = new Scanner(myObj);
        StringBuilder data = new StringBuilder();
        while (myReader.hasNextLine()) {
            data.append(myReader.nextLine()).append("\n");
        }
        myReader.close();
        return data.toString();
    }

    public static void zipFile(String sourceDirPath, String zipFilePath) throws IOException {
        try {
            Path p = Files.createFile(Paths.get(zipFilePath));

            ArrayList<Exception> exceptions = new ArrayList<>();
            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
                Path pp = Paths.get(sourceDirPath);
                try (Stream<Path> files = Files.walk(pp)) {
                    files
                            .filter(path -> !Files.isDirectory(path))
                            .forEach(path -> {
                                ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                                try {
                                    zs.putNextEntry(zipEntry);
                                    Files.copy(path, zs);
                                    zs.closeEntry();
                                } catch (IOException e) {
                                    MessageManager.log(String.format("Unable to zip %s to %s!", sourceDirPath, zipFilePath), LogLevel.ERROR, e);
                                    exceptions.add(e);
                                }
                            });
                }
            }
            if(!exceptions.isEmpty()) throw new IOException(exceptions.get(0));
        } catch (Exception e) {
            MessageManager.log(String.format("Encountered an unexpected exception trying to zip %s to %s!", sourceDirPath, zipFilePath), LogLevel.ERROR, e);
            throw new IOException(e);
        }
    }
}
