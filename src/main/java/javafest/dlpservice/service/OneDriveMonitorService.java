package javafest.dlpservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafest.dlpservice.utils.NotificationUtility;

@Service
public class OneDriveMonitorService {

    // private static final String ONE_DRIVE_DIRECTORY =
    // "C:\\Users\\tonmoy\\OneDrive - BUET";
    private final String ONE_DRIVE_DIRECTORY = findOneDriveDirectory();

    private static final String ICON_PATH = "/icons/onedrive.png"; // Define the icon directory

    private Set<String> knownFiles = new HashSet<>();
    private static final Logger logger = LoggerFactory.getLogger(OneDriveMonitorService.class);

    @Autowired
    private NotificationUtility notificationUtility;

    private String findOneDriveDirectory() {
        String userHome = System.getProperty("user.home");
        File homeDir = new File(userHome);

        // Search for directories that start with "OneDrive -" first
        File[] matchingDirs = homeDir
                .listFiles((dir, name) -> name.startsWith("OneDrive -") && new File(dir, name).isDirectory());

        if (matchingDirs != null && matchingDirs.length > 0) {
            // Return the first matching directory with "OneDrive -"
            logger.info("############## Specific OneDrive directory found: {}", matchingDirs[0].getAbsolutePath());
            return matchingDirs[0].getAbsolutePath();
        }

        // If no "OneDrive -" folder is found, fall back to default "OneDrive"
        File defaultOneDrive = new File(homeDir, "OneDrive");
        if (defaultOneDrive.exists() && defaultOneDrive.isDirectory()) {
            logger.info("############## Default OneDrive directory found: {}", defaultOneDrive.getAbsolutePath());
            return defaultOneDrive.getAbsolutePath();
        }

        logger.error("No OneDrive directory found in {}", userHome);
        throw new RuntimeException("'OneDrive' directory not found");
    }

    // Load initial state of files when the service starts
    @PostConstruct
    public void init() {
        logger.info("############# Initializing OneDriveMonitorService and loading initial file state #########");
        File directory = new File(ONE_DRIVE_DIRECTORY);

        if (directory.exists()) {
            knownFiles.addAll(Arrays.asList(directory.list())); // Load existing files
            logger.info("Initial files loaded: {}", knownFiles);
        } else {
            logger.error("Directory does not exist: {}", ONE_DRIVE_DIRECTORY);
        }
    }

    // Polling interval: every 2 seconds
    @Scheduled(fixedRate = 2000)
    @Async
    public void checkForNewFiles() {
        logger.debug("Checking for new files in OneDrive directory...");

        File directory = new File(ONE_DRIVE_DIRECTORY);
        if (!directory.exists()) {
            logger.error("Directory does not exist: {}", ONE_DRIVE_DIRECTORY);
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            Set<String> currentFiles = new HashSet<>(Arrays.asList(directory.list()));

            // Detect new files
            for (String fileName : currentFiles) {
                if (!knownFiles.contains(fileName)) {
                    logger.info("New file detected: {}", fileName);
                    try {
                        Path filePath = Paths.get(ONE_DRIVE_DIRECTORY, fileName);
                        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);

                        // Log detailed file info
                        logger.debug("File Name: {}", fileName);
                        logger.debug("File Size: {} bytes", attrs.size());
                        logger.debug("Creation Time: {}", attrs.creationTime());
                        logger.debug("Last Modified Time: {}", attrs.lastModifiedTime());

                        // Handle file copy (stop or delete)
                        stopFileCopy(fileName);

                        // Notify user

                        notificationUtility.notifyUser("Action Blocked", "Detected OneDrive Pasting", ICON_PATH);

                        sendNotification(fileName);
                    } catch (Exception e) {
                        logger.error("Error while handling file: " + fileName, e);
                    }
                }
            }

            // Update known files with the current state
            knownFiles = currentFiles;
        } else {
            logger.warn("No files detected in the directory. Is the directory path correct?");
        }
    }

    private void stopFileCopy(String fileName) {
        try {
            Path filePath = Paths.get(ONE_DRIVE_DIRECTORY, fileName);
            Files.delete(filePath);
            logger.info("File copy stopped: {}", fileName);
        } catch (Exception e) {
            logger.error("Failed to stop file copy for: " + fileName, e);
        }
    }

    private void sendNotification(String fileName) {
        // Simulating a notification
        logger.info("Notification sent: File {} was blocked from being copied.", fileName);
    }
}
