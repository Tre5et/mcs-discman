package net.treset.minecraft_server_discord_bot.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatTools {

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
            MessageManager.log(String.format("Unable to convert string \"%s\" to int.", input), LogLevel.WARN);
            return defaultValue;
        }
    }

    public static JsonElement parseJson(String jsonString) {
        try {
            return JsonParser.parseString(jsonString);
        } catch (IllegalStateException e) {
            MessageManager.log(String.format("Unable to parse json=%s;\n%s", jsonString, e), LogLevel.ERROR);
        }
        return new JsonObject();
    }
}
