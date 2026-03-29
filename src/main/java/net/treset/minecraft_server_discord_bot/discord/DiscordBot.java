package net.treset.minecraft_server_discord_bot.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.treset.minecraft_server_discord_bot.SlashCommandHandler;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import javax.security.auth.login.LoginException;

public class DiscordBot {
    public static JDA JDA;

    public static void initClient(String token) throws LoginException, InterruptedException {
        JDA = JDABuilder.createDefault(token)
                .addEventListeners(new SlashCommandHandler())
                .build();
        JDA.awaitReady();

        Logger.info("Client initialized.");
    }

    public static void sendText(String text, MessageOrigin org) {
        //TODO: remove sendMessageToDiscord(text);

        Logger.info("Sent message \"%s\" %s.", text, org.getMessage());
    }
}
