package com.cloudq.cloudq.service;

import com.cloudq.cloudq.exeption.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.storage.location}") String fileStorageLocation) {
        this.fileStorageLocation = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory where uploaded files will be stored.", e);
        }
    }

    /**
     * Store a file with a unique identifier.
     *
     * @param file the file to be stored.
     * @return the unique filename.
     */
    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + "." + fileExtension;

        try {
            // Check for invalid file paths
            if (originalFilename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + originalFilename);
            }

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            return uniqueFilename;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    /**
     * Load a file as a Path.
     *
     * @param filename the name of the file.
     * @return the file's path.
     */
    public Path loadFile(String filename) {
        return fileStorageLocation.resolve(filename).normalize();
    }

    /**
     * Delete a file by filename.
     *
     * @param filename the name of the file to be deleted.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = loadFile(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + filename + ". Please try again!", ex);
        }
    }

    /**
     * Get the file extension from a filename.
     *
     * @param filename the filename.
     * @return the file extension.
     */
    private String getFileExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf(".");
        return lastIndexOfDot == -1 ? "" : filename.substring(lastIndexOfDot + 1);
    }
}