package javafest.dlpservice.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafest.dlpservice.utils.NotificationUtility;
import javafest.dlpservice.utils.RefreshExplorer;

public class USBMonitorService implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(USBMonitorService.class);
    private final NotificationUtility notificationService = new NotificationUtility();

    private static final int MAX_RETRIES = 50;
    private static final int RETRY_DELAY_MS = 100; // set low value to seamlessly delete the file

    private final String usbPath;

    public USBMonitorService(String usbPath) {
        this.usbPath = usbPath;
    }

    @Override
    public void run() {
        try {
            monitorDirectory(Paths.get(usbPath));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void monitorDirectory(Path path) throws IOException, InterruptedException {
        logger.info("Monitoring USB directory: " + path);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Use poll with timeout instead of take to check for interruptions
                key = watchService.poll(10, TimeUnit.SECONDS);
                if (key == null) {
                    continue; // No events, continue loop
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path filePath = path.resolve((Path) event.context());
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    logger.info("File created: " + filePath);
                    if (containsKeyword(filePath, "secretKeyword")) {
                        String fileName = filePath.getFileName().toString();
                        logger.warn("Restricted data detected in file: " + fileName);

                        // check if file exists before deleting
                        if (Files.exists(filePath)) {
                            Files.delete(filePath);
                            RefreshExplorer.execute();
                            notificationService.notifyUser("Restricted data detected in file: " + fileName);
                        }
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
        watchService.close();
    }

    private boolean containsKeyword(Path filePath, String keyword) throws IOException {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            if (!Files.exists(filePath))
                break;
            try {
                String content = new String(Files.readAllBytes(filePath));
                if (content.contains(keyword)) {
                    return true;
                }
                return false;
            } catch (FileSystemException e) {
                attempts++;
                if (attempts >= MAX_RETRIES) {
                    throw e;
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }
}
