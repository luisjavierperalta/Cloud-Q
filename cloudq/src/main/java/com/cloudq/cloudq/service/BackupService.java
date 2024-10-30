package com.cloudq.cloudq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);
    private static final String BACKUP_DIRECTORY = "/path/to/backup/directory";
    private static final String DATA_DIRECTORY = "/path/to/data/directory";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public BackupService() {
        // Ensure backup directory exists
        File backupDir = new File(BACKUP_DIRECTORY);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
    }

    /**
     * Create a backup of the data directory.
     */
    public void createBackup() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String backupFileName = BACKUP_DIRECTORY + "/backup_" + timestamp + ".zip";

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFileName))) {
            Path sourceDir = Paths.get(DATA_DIRECTORY);
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            logger.error("Failed to add file to zip: " + path, e);
                        }
                    });
            logger.info("Backup created successfully at: {}", backupFileName);
        } catch (IOException e) {
            logger.error("Failed to create backup", e);
            throw new RuntimeException("Backup creation failed", e);
        }
    }

    /**
     * Restore the latest backup.
     */
    public void restoreBackup() {
        try {
            File latestBackup = getLatestBackupFile();
            if (latestBackup == null) {
                logger.warn("No backup found to restore");
                return;
            }

            Path restoreDir = Paths.get(DATA_DIRECTORY);
            try (FileInputStream fis = new FileInputStream(latestBackup);
                 ZipInputStream zis = new ZipInputStream(fis)) {
                Files.walk(restoreDir)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                logger.error("Failed to delete file during restore: " + path, e);
                            }
                        });

                ZipEntry zipEntry;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    File newFile = new File(restoreDir.toFile(), zipEntry.getName());
                    Files.createDirectories(newFile.getParentFile().toPath());
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                logger.info("Backup restored successfully from: {}", latestBackup.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Failed to restore backup", e);
            throw new RuntimeException("Backup restoration failed", e);
        }
    }

    /**
     * Get the most recent backup file from the backup directory.
     */
    private File getLatestBackupFile() {
        File backupDir = new File(BACKUP_DIRECTORY);
        File[] backups = backupDir.listFiles((dir, name) -> name.endsWith(".zip"));

        if (backups == null || backups.length == 0) {
            return null;
        }

        return Arrays.stream(backups)
                .max(Comparator.comparing(File::lastModified))
                .orElse(null);
    }

    /**
     * Schedule backups to occur at midnight every day.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledBackup() {
        logger.info("Scheduled backup initiated");
        createBackup();
    }
}