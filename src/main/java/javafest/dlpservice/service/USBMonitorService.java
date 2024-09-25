package javafest.dlpservice.service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
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
    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;

    public USBMonitorService(String usbPath) throws IOException {
        this.usbPath = usbPath;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            registerAll(Paths.get(usbPath));
            processEvents();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    // Register the given directory, and all its sub-directories.
    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // Register the given directory with the WatchService
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        keys.put(key, dir);
    }

    private void processEvents() throws InterruptedException, IOException {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
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

            Path dir = keys.get(key);
            if (dir == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path name = (Path) event.context();
                Path child = dir.resolve(name);

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerAll(child);
                    } else {
                        logger.info("File created: " + child);
                        if (containsKeyword(child, "secretKeyword")) {
                            String fileName = child.getFileName().toString();
                            logger.warn("Restricted data detected in file: " + fileName);

                            // check if file exists before deleting
                            if (Files.exists(child)) {
                                Files.delete(child);
                                RefreshExplorer.execute();
                                notificationService.notifyUser("Action Blocked", "File Contains Sensitive Data",
                                        "/icons/usb.png");
                            }
                        }
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    if (Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {
                        logger.info("File modified: " + child);
                        if (containsKeyword(child, "secretKeyword")) {
                            String fileName = child.getFileName().toString();
                            logger.warn("Restricted data detected in file: " + fileName);

                            // check if file exists before deleting
                            if (Files.exists(child)) {
                                Files.delete(child);
                                RefreshExplorer.execute();
                                notificationService.notifyUser("Restricted data detected", fileName, "/icons/usb.png");
                            }
                        }
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break; // all directories are inaccessible
                }
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
