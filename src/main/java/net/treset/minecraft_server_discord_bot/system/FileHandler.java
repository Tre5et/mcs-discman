package net.treset.minecraft_server_discord_bot.system;

import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHandler {
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
                                if(!path.getFileName().toString().equals("session.lock")) {
                                    ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                                    try {
                                        zs.putNextEntry(zipEntry);
                                        Files.copy(path, zs);
                                        zs.closeEntry();
                                    } catch (IOException e) {
                                        Logger.error(e, "Unable to zip %s to %s!", sourceDirPath, zipFilePath);
                                        exceptions.add(e);
                                    }
                                }
                            });
                }
            }
            if(!exceptions.isEmpty()) throw new IOException(exceptions.get(0));
        } catch (Exception e) {
            Logger.error(e, "Encountered an unexpected exception trying to zip %s to %s!", sourceDirPath, zipFilePath);
            throw new IOException(e);
        }
    }
}
