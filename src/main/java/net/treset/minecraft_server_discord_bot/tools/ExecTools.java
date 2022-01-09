package net.treset.minecraft_server_discord_bot.tools;

import net.treset.minecraft_server_discord_bot.DiscordBot;

import java.io.*;

public class ExecTools {
    public static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();

        File tempScript = createTempScript(command);

        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("sh", tempScript.toString());

            Process p = pb.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line).append("\n");
            }

            int exitCode = p.waitFor();
            assert exitCode == 0;

        } catch (Exception e) {
            DiscordBot.LOGGER.warn(String.format("ExecTools failed: couldn't execute command \"%s\"", command));
            e.printStackTrace();
        }

        return output.toString();
    }

    public static File createTempScript(String com) {
        File tempScript = null;
        try {
            tempScript = File.createTempFile("script", null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Writer streamWriter = null;
        try {
            assert tempScript != null;
            streamWriter = new OutputStreamWriter(new FileOutputStream(
                    tempScript));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert streamWriter != null;
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");
        printWriter.println(com);

        printWriter.close();

        return tempScript;
    }
}
