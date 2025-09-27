package net.treset.minecraft_server_discord_bot.upload;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveClient {
    private static final String APPLICATION_NAME = "MCS-Discman";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static Drive SERVICE = null;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(Config.drive.drive_credentials_file);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                //.setScopes(new ArrayList<String>(Collections.singleton("https://www.googleapis.com/auth/drive")))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void init() {
        if(!Config.drive.enabled) {
            Logger.info("Not creating drive client because it is not configured.");
            return;
        }

        // Build a new authorized API client service.
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            SERVICE = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            Logger.info("Drive client initialized.");
        } catch (IOException | GeneralSecurityException e) {
            DiscordBot.sendText("Unable to create drive client but drive features are enabled. Disabling for now. Please fix this manually.", MessageOrigin.SCHEDULE);
            Logger.warn(e, "Unable to create Drive client. Disabling drive features.");
            Config.drive.enabled = false;
        }
    }

    public static String uploadFile(String path, String name, String fileMIME, String folder) {
        if(!Config.drive.enabled) return "local";
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setParents(Collections.singletonList(folder));
        fileMetadata.setMimeType(fileMIME);
        java.io.File fileContent = new java.io.File(path);
        FileContent mediaContent = new FileContent(fileMIME, fileContent);
        File file;
        try {
            file = SERVICE.files().create(fileMetadata, mediaContent).setFields("id").execute();
        } catch (IOException e) {
            Logger.warn(e, "Failed to upload file to drive.");
            return null;
        }
        return file.getId();
    }
}
