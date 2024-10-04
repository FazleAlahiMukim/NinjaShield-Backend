package javafest.dlpservice.service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafest.dlpservice.dto.Action;
import javafest.dlpservice.utils.RefreshExplorer;

public class USBMonitorService implements Runnable {

    private PolicyCheckService policyCheckService;

    private static final Logger logger = LoggerFactory.getLogger(USBMonitorService.class);

    private final String usbPath;
    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;

    public USBMonitorService(String usbPath, PolicyCheckService policyCheckService) throws IOException {
        this.usbPath = usbPath;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.policyCheckService = policyCheckService;
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

    
    private final Set<Path> processedFiles = Collections.newSetFromMap(new HashMap<>());

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
                    if (processedFiles.contains(child)) {
                        continue;
                    }
                    processedFiles.add(child);
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerAll(child);
                    } else {
                        logger.info("File created: " + child);
                        Action action = search(child);
                        if (action != null && action.getAction().equals("block")) {
                            String fileName = child.getFileName().toString();
                            logger.warn("Restricted data detected in file: " + fileName);

                            if (Files.exists(child)) {
                                int attempts = 0;
                                while (attempts < 10) {
                                    try {
                                        Files.delete(child);
                                        break;
                                    } catch (IOException e) {
                                        attempts++;
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException interruptedException) {
                                            Thread.currentThread().interrupt();
                                            break;
                                        }
                                    }
                                }
                                logger.info("File deleted: " + fileName);
                                RefreshExplorer.execute();
                            }
                        }
                        scheduleFileCleanup(child);
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

    private void scheduleFileCleanup(Path child) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                processedFiles.remove(child);
            }
        }, TimeUnit.SECONDS.toMillis(10));
    }

    private Action search(Path filePath) throws IOException {
        int attempts = 0;
        while (attempts < 50) {
            if (!Files.exists(filePath)){
                attempts++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            }
            String filePathString = filePath.toString();
            return policyCheckService.getActionForFile("Removable storage", filePathString);
        }
        return null;
    }
}
