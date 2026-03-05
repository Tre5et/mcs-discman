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
import net.treset.minecraft_server_discord_bot.config.GoogleDriveConfig;
import net.treset.minecraft_server_discord_bot.exception.UploadException;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveUploadService implements UploadService {
    private static final String APPLICATION_NAME = "MCS-Discman";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private final GoogleDriveConfig config;

    private final Drive service;

    public GoogleDriveUploadService(GoogleDriveConfig config) throws UploadException {
        this.config = config;

        if(config == null) {
            throw new UploadException("Not creating drive client because it is not configured.");
        }

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            Logger.info("Drive client initialized.");
        } catch (IOException | GeneralSecurityException e) {
            throw new UploadException("Unable to create drive client.", e);
        }
    }


    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = new FileInputStream(config.credentialsFile);
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

    @Override
    public String name() {
        return "Google Drive";
    }

    @Override
    public void upload(File sourceFile, String targetPath, String mimeType) throws UploadException {
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(targetPath);
        fileMetadata.setParents(Collections.singletonList(config.folderId));
        fileMetadata.setMimeType(mimeType);
        FileContent mediaContent = new FileContent(mimeType, sourceFile);
        try {
            service.files().create(fileMetadata, mediaContent).setFields("id").execute();
        } catch (IOException e) {
            throw new UploadException("Failed to upload file to google drive.", e);
        }
    }
}
