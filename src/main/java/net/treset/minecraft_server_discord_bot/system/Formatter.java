package net.treset.minecraft_server_discord_bot.system;

import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    public static String formatList(List<String> list, String seperator) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if(i == list.size() - 1) {
                output.append(list.get(i));
            } else {
                output.append(list.get(i)).append(seperator);
            }
        }

        return output.toString();
    }

    public static String findStringBetween(String input, String str1, String str2){
        String regex = String.format("(?:%s)(.*?)(?:%s)", str1, str2);

        Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

        return matchRegex(input, p);
    }

    public static String matchRegex(String input, Pattern pattern) {
        String output = "";

        Matcher m = pattern.matcher(input);

        if (m.find()) {
            output = m.group(1);
        }

        return output;
    }

    public static boolean stringToBoolean(String input) {
        return stringToBoolean(input, false);
    }

    public static boolean stringToBoolean(String input, boolean defaultValue) {
        if(input == null || input.isBlank()) {
            return defaultValue;
        }
        return input.equals("true") || input.equals("enabled");
    }

    public static int stringToInt(String input) {
        return stringToInt(input, -1);
    }
    public static int stringToInt(String input, int defaultValue) {
        if(input == null || input.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            Logger.warn("Unable to convert string \"%s\" to int.", input);
            return defaultValue;
        }
    }
}
