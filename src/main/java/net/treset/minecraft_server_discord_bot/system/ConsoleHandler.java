package net.treset.minecraft_server_discord_bot.system;

import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.io.*;
import java.util.List;

public class ConsoleHandler {
    public static Process startProcess(List<String> command, String wd) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(command);
        pb.directory(new File(wd));
        return pb.start();
    }

    public static File createTempScript(String com) {
        File tempScript = null;
        try {
            tempScript = File.createTempFile("script", null);
        } catch (IOException e) {
            Logger.error(e, "Failed to create temporary script");
        }

        Writer streamWriter = null;
        try {
            assert tempScript != null;
            streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
        } catch (FileNotFoundException e) {
            Logger.error(e, "Failed to write to temporary script");
        }

        assert streamWriter != null;
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");
        printWriter.println(com);

        printWriter.close();

        return tempScript;
    }
}
