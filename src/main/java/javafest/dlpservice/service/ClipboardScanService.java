package javafest.dlpservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javafest.dlpservice.utils.NotificationUtility;

@Service
public class ClipboardScanService {

    private static final Logger logger = LoggerFactory.getLogger(ClipboardScanService.class);
    private static final String KEYWORD = "Keyword"; // Define the keyword to search for
    private static final String ICON_PATH = "/icons/clipboard.png"; // Define the icon directory

    @Autowired
    private NotificationUtility notificationUtility;

    @Async
    @Scheduled(fixedDelay = 5000) // Scans clipboard every 8 seconds
    public void scanClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            // Check if the clipboard contains string data
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                String clipboardContent = (String) clipboard.getData(DataFlavor.stringFlavor);

                if (clipboardContent != null && clipboardContent.contains(KEYWORD)) {
                    logger.info("!!!!!!!!!! KeyWord found in clipboard content !!!!!!!!!!");
                    notificationUtility.notifyUser("Action Blocked", "Keyword found in clipboard.", ICON_PATH);
                } else {
                    logger.info("KeyWord not found in clipboard.");
                }
            } else {
                logger.info("Clipboard does not contain string data.");
            }

        } catch (UnsupportedFlavorException | IOException e) {
            logger.error("Failed to access clipboard content", e);
        }
    }
}
