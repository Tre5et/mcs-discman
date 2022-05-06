package net.treset.minecraft_server_discord_bot.tools;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatTools {

    public static String formatList(List<String> list, String seperator) {
        String output = "";

        for (int i = 0; i < list.size(); i++) {
            if(i == list.size() - 1) {
                output += list.get(i);
            } else {
                output += list.get(i) + seperator;
            }
        }

        return output;
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
        return input.equals("true") || input.equals("enabled");
    }

    public static int stringToInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
