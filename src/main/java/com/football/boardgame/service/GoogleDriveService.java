package com.football.boardgame.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "FootballBoardGame Backup";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE = "/config/google/credentials.json";
    // Tokens stored in project working dir so they persist between restarts
    private static final String TOKENS_DIR = "data/tokens";
    private static final String DRIVE_FOLDER_NAME = "FootballBoardGame Backups";

    private Drive getDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = GoogleDriveService.class.getResourceAsStream(CREDENTIALS_FILE);
        if (in == null) {
            throw new IOException("credentials.json no encontrado en: " + CREDENTIALS_FILE);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                transport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIR)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Drive.Builder(transport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Uploads a local file to the 'FootballBoardGame Backups' folder in Google Drive.
     * Creates the folder if it doesn't exist yet.
     *
     * @param localFile Path to the local file to upload
     * @param mimeType  MIME type of the file (e.g. "application/json", "application/sql")
     * @return The Drive file ID of the uploaded file
     */
    public String uploadBackup(Path localFile, String mimeType) throws IOException, GeneralSecurityException {
        Drive drive = getDriveService();

        String folderId = getOrCreateFolder(drive);

        File fileMetadata = new File();
        fileMetadata.setName(localFile.getFileName().toString());
        fileMetadata.setParents(Collections.singletonList(folderId));

        FileContent mediaContent = new FileContent(mimeType, localFile.toFile());

        File uploaded = drive.files().create(fileMetadata, mediaContent)
                .setFields("id, name")
                .execute();

        log.info("Backup subido a Drive: {} (id: {})", uploaded.getName(), uploaded.getId());
        return uploaded.getId();
    }

    private String getOrCreateFolder(Drive drive) throws IOException {
        // Search for existing folder
        FileList result = drive.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder' and name='" + DRIVE_FOLDER_NAME + "' and trashed=false")
                .setFields("files(id, name)")
                .execute();

        if (result.getFiles() != null && !result.getFiles().isEmpty()) {
            String folderId = result.getFiles().get(0).getId();
            log.info("Carpeta Drive encontrada: {} (id: {})", DRIVE_FOLDER_NAME, folderId);
            return folderId;
        }

        // Create folder
        File folderMetadata = new File();
        folderMetadata.setName(DRIVE_FOLDER_NAME);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        File folder = drive.files().create(folderMetadata)
                .setFields("id")
                .execute();

        log.info("Carpeta Drive creada: {} (id: {})", DRIVE_FOLDER_NAME, folder.getId());
        return folder.getId();
    }
}
